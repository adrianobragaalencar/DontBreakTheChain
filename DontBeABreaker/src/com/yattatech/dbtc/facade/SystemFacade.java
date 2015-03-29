/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.facade;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.apache.commons.io.IOUtils;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.yattatech.dbtc.Constants;
import com.yattatech.dbtc.activity.AsyncMessages;
import com.yattatech.dbtc.dao.SystemDao;
import com.yattatech.dbtc.dao.SystemPreferences;
import com.yattatech.dbtc.domain.Check;
import com.yattatech.dbtc.domain.Task;
import com.yattatech.dbtc.domain.TaskList;
import com.yattatech.dbtc.domain.TwitterLoginStatus;
import com.yattatech.dbtc.io.BackupInputStream;
import com.yattatech.dbtc.log.Debug;
import com.yattatech.dbtc.receiver.Broadcaster;
import com.yattatech.dbtc.util.TwitterUtil;

/**
 * Facade as entry point to all application 
 * logic.
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com) 
 * 
 */
public final class SystemFacade {

    public static final String TAG          = "SystemFacade";
    public static final SystemFacade UNIQUE = new SystemFacade();
    private final SystemDao mDao            = new SystemDao();
    private final SystemPreferences mPrefs  = new SystemPreferences();
    private final ExecutorService mExecutor;
    
    private SystemFacade() {    
        mExecutor = Executors.newFixedThreadPool(Constants.DEFAULT_THREADS_NUM);
        // To guarantee that executor will be killed as soon as 
        // vm halt
        Runtime.getRuntime().addShutdownHook(new Thread() {
            
            /*
             * (non-Javadoc)
             * @see java.lang.Thread#run()
             */
            @Override
            public void run() {
                mExecutor.shutdown();
            }          
        });
    }
    
    public void editTask(final Task task) {
    	if (task == null) {
    		throw new IllegalArgumentException("Task cannot be null");
    	}
    	Debug.d(TAG, "editTask");
    	mExecutor.execute(new Runnable() {
            
    	    /*
    	     * (non-Javadoc)
    	     * @see java.lang.Runnable#run()
    	     */
            @Override
            public void run() {
                final Task returned = mDao.editTask(task);
                final Intent intent = new Intent(AsyncMessages.EDIT_TASK_ACTION);
                // To avoid messing around with Serializable overload
                // method
                intent.putExtra(Constants.TASK_KEY, (Parcelable)returned);
                Broadcaster.sendLocalMessage(intent);
            }
        });    		
    }
    
    public void restoreTasks(final List<Task> tasks) {
        if (Debug.isDebugable()) {
            Debug.d(TAG, "restoreTasks tasks=" + tasks);    
        }
        mExecutor.execute(new Runnable() {
            
            /*
             * (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
                final boolean result = mDao.restoreTasks(tasks);
                final Intent intent  = new Intent(AsyncMessages.BACKUP_RESTORE_ACTION);
                intent.putExtra(Constants.RESTORE_KEY, result);
                Broadcaster.sendLocalMessage(intent);           
            }
        });
    }
    
    public void addNewTask(final String name, final boolean defined, final int maxDays) {
    	if (name == null) {
    		throw new IllegalArgumentException("Taskname cannot be null");
    	}
    	Debug.d(TAG, "addNewTask");
    	mExecutor.execute(new Runnable() {
            
    	    /*
    	     * (non-Javadoc)
    	     * @see java.lang.Runnable#run()
    	     */
            @Override
            public void run() {
                final Task task     = mDao.addNewTask(name, defined, maxDays);
                final Intent intent = new Intent(AsyncMessages.ADD_NEW_TASK_ACTION);
                // To avoid messing around with Serializable overload
                // method
                intent.putExtra(Constants.TASK_KEY, (Parcelable)task);
                Broadcaster.sendLocalMessage(intent);
            }
        });
    }
    
    public void removeTask(final Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        Debug.d(TAG, "removeTask");
        mExecutor.execute(new Runnable() {
            
            /*
             * (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
                final Task returned = mDao.removeTask(task);
                final Intent intent = new Intent(AsyncMessages.REMOVE_TASK_ACTION);
                intent.putExtra(Constants.TASK_KEY, (Parcelable)returned);
                Broadcaster.sendLocalMessage(intent);
            }
        });
    }
    
    public List<Task> getTasks() {
    	Debug.d(TAG, "getTasks");
    	return mDao.getTasks();
    }
    
    private List<Task> getTasksWithChecks() {
    	Debug.d(TAG, "getTasksWithChecks");
    	return mDao.getTasksWithChecks();
    }
    
    public boolean isTwitterLogged() {
    	Debug.d(TAG, "isTwitterLogged");
    	return mPrefs.isTwitterLogged();
    }
    
    public void setTwitterData(final Bundle bundle) {
    	Debug.d(TAG, "setTwitterData");
    	mExecutor.execute(new Runnable() {
    		
    		/*
    		 * (non-Javadoc)
    		 * @see java.lang.Runnable#run()
    		 */
    		@Override
    		public void run() {
    		    final TwitterLoginStatus status = mPrefs.setTwitterData(bundle);
    		    if (Debug.isDebugable()) {
    		        Debug.d(TAG, "setTwitterData=" + status.name());
    		    }
                final Intent intent             = new Intent(AsyncMessages.TWITTER_LOGIN_ACTION);
                // To avoid messing around with Serializable overload
                // method
                intent.putExtra(Constants.TWITTER_LOGIN_KEY, (Parcelable)status);
                Broadcaster.sendLocalMessage(intent);
    		    
    		}
    	});    	
    }
    
    public void sendTwitterMsg(final String msg) {
    	if (Debug.isDebugable()) {
    		Debug.d(TAG, "sendTwitterMsg " + msg);
    	}
    	mExecutor.execute(new Runnable() {

    		/*
    		 * (non-Javadoc)
    		 * @see java.lang.Runnable#run()
    		 */
    		@Override
    		public void run() {
				final Twitter twitter = TwitterUtil.getTwitter();
				try {
					twitter.setOAuthAccessToken(mPrefs.getTwitterAccessToken());
					twitter.updateStatus(msg);
				} catch (TwitterException te) {
					Debug.d(TAG, "Failed: ", te);
				}
    		}
    	});
    }
    
    public void twitterLogout() {
        Debug.d(TAG, "twitterLogout");
        mExecutor.execute(new Runnable() {

            /*
             * (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
                mPrefs.twitterLogout();
            }
        });        
    }
    
    public void dropboxLogout() {
        Debug.d(TAG, "dropboxLogout");
        mExecutor.execute(new Runnable() {

            /*
             * (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
                mPrefs.dropboxLogout();
            }
        });                
    }
    
    public void setDropBoxToken(final String token) {
        Debug.d(TAG, "setDropBoxToken");
        mExecutor.execute(new Runnable() {

            /*
             * (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
                mPrefs.setDropBoxToken(token);
            }
        });                
    }
    
    public boolean hasTasks() {
    	Debug.d(TAG, "getDropBoxToken");
    	return mDao.hasTasks();
    }
    
    public String getDropBoxToken() {
        Debug.d(TAG, "getDropBoxToken");
        return mPrefs.getDropBoxToken();
    }
    
    public void saveCheck(final int year, final int month, final int day, final int taskId) {
        if (Debug.isDebugable()) {
            Debug.d(TAG, "check year=" + year  + 
                          " month="     + month +
                          " day="       + day   +
                          " taskId="    + taskId); 
        }        
        mExecutor.execute(new Runnable() {

            /*
             * (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
                mDao.saveCheck(year, month, day, taskId);
            }
        });
    }
    
    public void removeCheck(final int year, final int month, final int day, final int taskId) {
        if (Debug.isDebugable()) {
            Debug.d(TAG, "check year=" + year  + 
                          " month="     + month +
                          " day="       + day   +
                          " taskId="    + taskId); 
        }        
        mExecutor.execute(new Runnable() {

            /*
             * (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
                mDao.removeCheck(year, month, day, taskId);
            }
        });        
    }
    
    public BackupInputStream getBackupInputStream() throws IOException {
        Debug.d(TAG, "getBackupInputStream");
        final List<Task> tasks     = getTasksWithChecks();
        final TaskList taskList    = new TaskList();
        final GsonBuilder builder  = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        final Gson gson            = builder.create();
        final Type type            = new TypeToken<List<Task>>() {}.getType();
        final String json          = gson.toJson(tasks, type);
        taskList.mChecksum         = calculateChecksum(json);
        taskList.mTasks            = tasks;
        final String json2         = gson.toJson(taskList);
        final BackupInputStream in = new BackupInputStream(IOUtils.toInputStream(json2, "UTF-8"));
        in.mLength                 = json2.length();
        if (Debug.isDebugable()) {
            Debug.d(TAG, "tasks="   + tasks.size() +
                         " json="   + json         + 
                         " length=" + in.mLength);
        }
        return in;
    }
    
    public List<Task> getTasksFromJson(final String json) {
        if (Debug.isDebugable()) {
            Debug.d(TAG, "getTasksFromJson json=" + json);
        }        
        final Type type           = new TypeToken<List<Task>>() {}.getType();
        final GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        final Gson gson           = builder.create();
        
        final TaskList taskList = gson.fromJson(json, TaskList.class);
        if (taskList != null) {
        	final long checksum = taskList.mChecksum;
        	final String json2  = gson.toJson(taskList.mTasks, type);
        	if (isChecksumValid(checksum, json2)) {
        		return taskList.mTasks;
        	}
        }
        return null;
    }
    
    public List<Check> getChecksByTaskAndDate(final Task task, final Calendar date) {
    	if (Debug.isDebugable()) {
    		Debug.d(TAG, "getChecksByTaskAndDate task=" + task +
    				     "date="                        + date); 
    	}
    	return mDao.getChecksByTaskAndDate(task, date);
    }
    
    public int getChecksCountByTask(final Task task) {
    	if (Debug.isDebugable()) {
    		Debug.d(TAG, "getChecksCountByTask task=" + task); 
    	}
    	return mDao.getChecksCountByTask(task);
    }
    
    /**
     * Actually that only checks if current year is less than
     * Minimum one supported by application. That values is 
     * the 2nd position in {@link Constants#MIN_CALENDAR_DATE}
     * 
     * @return boolean 
     */
    public boolean isDeviceClockOutOfSync() {
    	final Calendar calendar = Calendar.getInstance();
    	calendar.setTimeInMillis(System.currentTimeMillis());
    	final int year          = calendar.get(Calendar.YEAR);
    	final int minYear       = Constants.MIN_CALENDAR_DATE[1];
    	if (Debug.isDebugable()) {
    		Debug.d(TAG, "isDeviceClockOutOfSync currentYear=" + year +
    				     "minimulSupportedYear="               + minYear);
    	}
    	return year < minYear;
    }
    
    public boolean isTaskChecked(Task task) {
        return mDao.isTaskChecked(task);
    }
    
    public void saveDataOrder(List<Task> tasks) {
        if (Debug.isDebugable()) {
            Debug.d(TAG, "saveDataOrder tasks=" + tasks);
        }
        mPrefs.saveDataOrder(tasks);
    }
    
    public List<Task> restoreDataOrder(List<Task> tasks) {
        if (Debug.isDebugable()) {
            Debug.d(TAG, "restoreDataOrder tasks=" + tasks);
        }
        return mPrefs.restoreDataOrder(tasks);
    }
    
    private long calculateChecksum(String json) {
    	final byte[] input       = json.getBytes();     
    	final Checksum checksum  = new CRC32();
    	checksum.update(input, 0, input.length);
    	final long checksumValue = checksum.getValue();
    	if (Debug.isDebugable()) {
    		Debug.d(TAG, "calculate checksum for json=" + json + 
    				     "\nvalue="                     + checksumValue);
    	}
    	return checksumValue;
    }
    
    private boolean isChecksumValid(long checksumValue, String json) {
    	final byte[] input       = json.getBytes();     
    	final Checksum checksum  = new CRC32();
    	checksum.update(input, 0, input.length);
    	final long returned      = checksum.getValue();
    	if (Debug.isDebugable()) {
    		Debug.d(TAG, "validate checksum for json="  + json          + 
    				     "\nexpected="                  + checksumValue + 
    				     "\nretrieve="                  + returned);
    	}
    	return checksumValue == returned;
    }
}
