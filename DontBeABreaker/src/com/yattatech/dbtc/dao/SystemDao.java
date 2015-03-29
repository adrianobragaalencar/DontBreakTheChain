/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.dao;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.yattatech.dbtc.DBTCApplication;
import com.yattatech.dbtc.domain.Check;
import com.yattatech.dbtc.domain.InternalDate;
import com.yattatech.dbtc.domain.Task;
import com.yattatech.dbtc.log.Debug;
import com.yattatech.dbtc.util.CurrentDateUtil;

/**
 * Dao object that takes care about of all IO database
 * operations
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class SystemDao {
	
	private static final String TAG               = "SystemDao";
	private static final DatabaseHelper DB_HELPER = OpenHelperManager.getHelper(DBTCApplication.sApplicationContext, DatabaseHelper.class);
	private static final int REMOVE_SUCCESS       = 1;
	private static final Check DUMMY_INSERT       = new Check();
	private final Calendar mCalendar              = Calendar.getInstance();
	private final Dao<Task, Integer> mTaskDao     = DB_HELPER.getDaoLayer(Task.class);
	private final Dao<Check, Integer> mCheckDao   = DB_HELPER.getDaoLayer(Check.class);

	/**
	 * A very dangerous method, here we really take care of many 
	 * operations as delete old data and submit new ones, but in
	 * meantime many things can be very badly. 
	 * 
	 * @param tasks
	 * 
	 */
	public boolean restoreTasks(final List<Task> tasks) {
        if (Debug.isDebugable()) {
            Debug.d(TAG, "restoreTasks tasks=" + tasks);   
        }
        deleteTable(mCheckDao);
        deleteTable(mTaskDao);
        for (Task task : tasks) {
        	CreateOrUpdateStatus status = createOrUpdate(mTaskDao, task);
        	if (!status.isCreated() && !status.isUpdated()) {
        		if (Debug.isDebugable()) {
        			Debug.d(TAG, "Task could not be created " + status);
        		}
        		continue;
        	}
            if (task.mChecks != null) {
            	for (Check check : task.mChecks) {
            		check.mTaskId = task.mId;
            		createOrUpdate(mCheckDao, check);
            	}
            }
        }
        return true;
	}
	
	public boolean hasTasks() {
		try {
			return mTaskDao.countOf() > 0;
		} catch (SQLException e) {
			Debug.d(TAG, "Failed:", e);
			return false;
		}
	}
	
	public Task addNewTask(String name, boolean defined, int maxDays) {
		if (Debug.isDebugable()) {
			Debug.d(TAG, "add new task=" + name);	
		}
		final Task task         = new Task();
		// There's no way to take care of real clock time, cause the user can
		// change it and application there's no way to know it's real or not
		// cause we don't make any remote server clock synchronization		
		synchronized (mCalendar) {
			mCalendar.setTime(new Date());			
			task.mName          = name;
			task.mDayCreation   = mCalendar.get(Calendar.DAY_OF_MONTH);
			task.mMonthCreation = mCalendar.get(Calendar.MONTH);
			task.mYearCreation  = mCalendar.get(Calendar.YEAR);
			task.mDefined       = defined;
			task.mMaxDays       = maxDays;
		}
		final CreateOrUpdateStatus status = createOrUpdate(mTaskDao, task);
		if ((status != null) && (status.isCreated())) {
		    return task;
		}
		return null;
	}
	
	public Task editTask(Task task) {
		final boolean degugable = Debug.isDebugable(); 
		if (degugable) {
			Debug.d(TAG, "edit task=" + task);	
		}
		final CreateOrUpdateStatus status = createOrUpdate(mTaskDao, task);
		if (task.mDefined) {
	    	final Calendar c = Calendar.getInstance();
	    	c.set(task.mYearCreation, task.mMonthCreation, task.mDayCreation);
	    	c.add(Calendar.DAY_OF_MONTH, task.mMaxDays - 1);    // current day as offset
	    	final int day    = c.get(Calendar.DAY_OF_MONTH);
	    	final int month  = c.get(Calendar.MONTH);
	    	final int year   = c.get(Calendar.YEAR);
	    	final int rows   = removeChecksGreaterThan(year, month, day, task.mId);
	    	if (degugable) {
	    		Debug.d(TAG, "Removing checks greater than day=" + day   + 
	    				     " month="                           + month +
	    				     " year="                            + year  +
	    				     " affected rows="                   + rows);
	    	}
		}		
		if (status != null) {
			if ((status.isCreated()) || (status.isUpdated())) {
				return task;	
			}		    
		}
		return null;		
	}
	
	public Task removeTask(Task task) {
	    if (Debug.isDebugable()) {
	        Debug.d(TAG, "removing task=" + task);
	    }
	    return delete(mTaskDao, task) == REMOVE_SUCCESS ? task : null;
	}
	
	public List<Task> getTasks() {	
	    Debug.d(TAG, "getTasks");
		return queryForAll(mTaskDao);
	}	
	
	public List<Task> getTasksWithChecks() {
		Debug.d(TAG, "getTasksWithChecks");
		final List<Task> tasks = getTasks();
		for (int i = 0, s = tasks.size(); i < s; ++i) {
			final Task task    = tasks.get(i);
			task.mChecks       = getChecksByTask(task);
		}
		return tasks;
	}

	/*
	 * Very expensive insert method, to avoid many allocation/deallocation
	 * of small objects, we gonna make that insert directly with no middle
	 * domain object 
	 * 
	 */
	public void saveCheck(final int year, final int month, final int day, final int taskId) {
		synchronized (DUMMY_INSERT) {
			DUMMY_INSERT.mYear   = year;
			DUMMY_INSERT.mMonth  = month; 
			DUMMY_INSERT.mDay    = day;
			DUMMY_INSERT.mTaskId = taskId;
			create(mCheckDao, DUMMY_INSERT);
		}
	}

	public void removeCheck(final int year, final int month, final int day, final int taskId) {
		final DeleteBuilder<Check, Integer> deleteBuilder = mCheckDao.deleteBuilder();
		try {				
			deleteBuilder.where()
						 .eq("task_id",     taskId)
						 .and().eq("month", month)
						 .and().eq("year",  year)							
						 .and().eq("day",   day);
			mCheckDao.delete(deleteBuilder.prepare());
		} catch (SQLException e) {
			Debug.d(TAG, "Failed:", e);
		} finally {
			deleteBuilder.reset();
		}		
	}	
	
	private int removeChecksGreaterThan(final int year, final int month, final int day, final int taskId) {
		final DeleteBuilder<Check, Integer> deleteBuilder = mCheckDao.deleteBuilder();
		try {				
			deleteBuilder.where()
						 .eq("task_id",     taskId)
						 .and().ge("month", month)
						 .and().ge("year",  year)							
						 .and().gt("day",   day);
			return mCheckDao.delete(deleteBuilder.prepare());
		} catch (SQLException e) {
			Debug.d(TAG, "Failed:", e);
		} finally {
			deleteBuilder.reset();
		}			
		return 0;
	}
	
	public List<Check> getChecksByTaskAndDate(final Task task, final Calendar date) {
		List<Check> checks = null;
		if ((task != null) && (date != null)) {
			final QueryBuilder<Check, Integer> queryBuilder = mCheckDao.queryBuilder();
			try {				
				final int month  = date.get(Calendar.MONTH);
				final int year   = date.get(Calendar.YEAR);
				final int maxDay = date.getActualMaximum(Calendar.DAY_OF_MONTH);
				queryBuilder.where()
							.eq("task_id",     task.mId)
							.and().eq("month", month)
							.and().eq("year",  year)							
							.and().ge("day",   1)
							.and().le("day",   maxDay);
				checks = mCheckDao.query(queryBuilder.prepare());
				
			} catch (SQLException e) {
				Debug.d(TAG, "Failed:", e);
			} finally {
				queryBuilder.reset();
			}
		}
		if (checks == null) {
			checks = Collections.<Check>emptyList();
		}
		return checks;
	}
	
	public int getChecksCountByTask(final Task task) {
		int countOf = 0;
		if (task != null) {
			final QueryBuilder<Check, Integer> queryBuilder = mCheckDao.queryBuilder();
			try {				
				countOf = (int)queryBuilder.where()
						                   .eq("task_id", task.mId)
						                   .countOf();								
			} catch (SQLException e) {
				Debug.d(TAG, "Failed:", e);
			} finally {
				queryBuilder.reset();
			}
		}
		return countOf;
	}
	
	public boolean isTaskChecked(Task task) {
	    final InternalDate currentDate = CurrentDateUtil.sCurrentDate;
        final QueryBuilder<Check, Integer> queryBuilder = mCheckDao.queryBuilder();
        try {               
            queryBuilder.where()
                        .eq("task_id",     task.mId)
                        .and().eq("month", currentDate.mMonth)
                        .and().eq("year",  currentDate.mYear)                            
                        .and().eq("day",   currentDate.mDay);
            return queryBuilder.countOf() > 0;
        } catch (SQLException e) {
            Debug.d(TAG, "Failed:", e);
            return false;
        } finally {
            queryBuilder.reset();
        }	     
	}
	
	private List<Check> getChecksByTask(final Task task) {
		List<Check> checks = null;
		if (task != null) {
			final QueryBuilder<Check, Integer> queryBuilder = mCheckDao.queryBuilder();
			try {				
				queryBuilder.where().eq("task_id", task.mId);
				checks = mCheckDao.query(queryBuilder.prepare());				
			} catch (SQLException e) {
				Debug.d(TAG, "Failed:", e);
			} finally {
				queryBuilder.reset();
			}
		}
		if (checks == null) {
			checks = Collections.<Check>emptyList();
		}
		return checks;		
	}
	
	private <T> void create(Dao<T, ?> dao, T t) {
	    try {
	        dao.create(t);
		} catch (SQLException e) {
			Debug.d(TAG, "Failed:", e);
		}	
	}
	
	private <T> CreateOrUpdateStatus createOrUpdate(Dao<T, ?> dao, T t) {
	    CreateOrUpdateStatus status = null;
	    try {
	        status = dao.createOrUpdate(t);
		} catch (SQLException e) {
			Debug.d(TAG, "Failed:", e);
		}	
	    if (Debug.isDebugable()) {
	        if (status == null) {
	            Debug.d(TAG, "createOrUpdate failed");
	        } else {
	            Debug.d(TAG, "createOrUpdate success=" + status);
	        }
	    }
	    return status;
	}
	
	private <T> int delete(Dao<T, ?> dao, T t) {
	    
	    int status = 0;
	    try {
	        status = dao.delete(t);
	    } catch (SQLException e) {
	            Debug.d(TAG, "Failed:", e);
	    }   
	    if (Debug.isDebugable()) {
	        // 1 indicates the one row has been affected
	        if (status == REMOVE_SUCCESS) {
	            Debug.d(TAG, "createOrUpdate failed");
	        } else {
	            Debug.d(TAG, "createOrUpdate success=" + status);
	        }
	    }
	    return status;
	}
	
	private <T> List<T> queryForAll(Dao<T, ?> dao) {
		try {
		    final List<T> elements = dao.queryForAll();
		    if (Debug.isDebugable()) {
		        final int size = (elements == null) ? 0 : elements.size();
		        Debug.d(TAG, "querying all=" + elements + 
		                     "\nsize="       + size);
		    }
		    return elements;
		} catch (SQLException e) {
			Debug.d(TAG, "Failed:", e);
		}			
		return Collections.<T>emptyList();
	}
	
	private void deleteTable(Dao<?, ?> dao) {      
        try {
            dao.deleteBuilder().delete();
        } catch (SQLException e) {
        	Debug.d(TAG, "Failed:", e);
        }
    }
}
