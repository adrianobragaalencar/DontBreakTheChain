/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.activity;

import static com.yattatech.dbtc.Constants.RESTORE_KEY;
import static com.yattatech.dbtc.Constants.TASK_KEY;
import static com.yattatech.dbtc.activity.AsyncMessages.ADD_NEW_TASK_ACTION;
import static com.yattatech.dbtc.activity.AsyncMessages.ADD_NEW_TASK_FAILED;
import static com.yattatech.dbtc.activity.AsyncMessages.ADD_NEW_TASK_SUCCESS;
import static com.yattatech.dbtc.activity.AsyncMessages.BACKUP_RESTORE_ACTION;
import static com.yattatech.dbtc.activity.AsyncMessages.BACKUP_RESTORE_FAILED;
import static com.yattatech.dbtc.activity.AsyncMessages.BACKUP_RESTORE_SUCCESS;
import static com.yattatech.dbtc.activity.AsyncMessages.EDIT_TASK_ACTION;
import static com.yattatech.dbtc.activity.AsyncMessages.EDIT_TASK_FAILED;
import static com.yattatech.dbtc.activity.AsyncMessages.EDIT_TASK_SUCCESS;
import static com.yattatech.dbtc.activity.AsyncMessages.REMOVE_TASK_ACTION;
import static com.yattatech.dbtc.activity.AsyncMessages.REMOVE_TASK_FAILED;
import static com.yattatech.dbtc.activity.AsyncMessages.REMOVE_TASK_SUCCESS;

import java.util.List;

import org.apache.commons.io.IOUtils;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxInputStream;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.session.AppKeyPair;
import com.mobeta.android.dslv.DragSortListView;
import com.yattatech.dbtc.Constants;
import com.yattatech.dbtc.R;
import com.yattatech.dbtc.adapter.TaskListAdapter;
import com.yattatech.dbtc.domain.Task;
import com.yattatech.dbtc.io.BackupInputStream;
import com.yattatech.dbtc.log.Debug;
import com.yattatech.dbtc.receiver.Broadcaster;
import com.yattatech.dbtc.util.DateUtil;
import com.yattatech.dbtc.util.StringUtils;

/**
 * Main Screen where the user can add/edit and delete 
 * items into the don't break the chain.
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class MainScreen extends GenericFragmentActivity {
	
    private enum DropBoxOp {
        SYNC,
        RESTORE,
        UNKNOWN
    }
    
    private DropboxAPI<AndroidAuthSession> mDropboxAPI;
	private TaskListAdapter mTaskListAdapter;
	private DragSortListView mTaskList;	
	private int mIndex;
	private String mToken;
	private UploadBackupAsyncTask mUploadBackupAsyncTask;
	private RestoreBackupAsyncTask mRestoreBackupAsyncTask;
	private DropBoxOp mDropBoxOp;
	private final OnItemClickListener mTaskListListener = new OnItemClickListener() {

		/*
		 * (non-Javadoc)
		 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
		 */
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (Debug.isDebugable()) {
				Debug.d(mTag, "onItemClick parent=" + parent   +
						      " view="              + view     +
						      " position="          + position +
						      " id="                + id);
			}
			mIndex = position;
            final Task task = getSelectedTask(false);
            if (task != null) {
                if (Debug.isDebugable()) {
                    Debug.d(mTag, "chaining task=" + task);
                }                
                startActivityWithTask(task, task.mDefined ? FixedChainTaskScreen.class : ChainTaskScreen.class);
            }
		}
	};
    private DragSortListView.DropListener mDropListener = new DragSortListView.DropListener() {
        
        /*
         * (non-Javadoc)
         * @see com.mobeta.android.dslv.DragSortListView.DropListener#drop(int, int)
         */
        @Override
        public void drop(int from, int to) {
            
            final Task item = mTaskListAdapter.getItem(from);
            mTaskListAdapter.removeElement(item);
            mTaskListAdapter.insertElementAt(item, to);
        }
    };
    private DragSortListView.DragScrollProfile mScrollProfile = new DragSortListView.DragScrollProfile() {
        
        /*
         * (non-Javadoc)
         * @see com.mobeta.android.dslv.DragSortListView.DragScrollProfile#getSpeed(float, long)
         */
        @Override
        public float getSpeed(float w, long t) {
            if (w > 0.8F) {
                return ((float) mTaskListAdapter.getCount()) / 0.001F;
            }
            return 10.0F * w;            
        }
    };
	private final Handler mH = new Handler() {
        
        /*
         * (non-Javadoc)
         * @see android.os.Handler#handleMessage(android.os.Message)
         */
        @Override
        public void handleMessage(Message msg) {     
            switch (msg.what) {
            case ADD_NEW_TASK_SUCCESS:
                Debug.d(mTag, "task added successfully, let's update ui");
                mTaskListAdapter.addElement((Task)msg.obj);                      
                break;
            case ADD_NEW_TASK_FAILED:
                Debug.d(mTag, "task add failed");
                showMessage(R.string.add_task_fail);
                break;                           	
            case EDIT_TASK_SUCCESS:
            	Debug.d(mTag, "task edited successfully, let's update ui");
            	final List<Task> tasks = mTaskListAdapter.getSource();
            	final Task task        = (Task)msg.obj;
            	for (int i = 0, s = tasks.size(); i < s; ++i) {
            		if (task.mId == tasks.get(i).mId) {
            			tasks.set(i, task);
            			mTaskListAdapter.notifyDataSetChanged();
            			break;
            		}            		
            	}           
            	break;
            case EDIT_TASK_FAILED:
            	Debug.d(mTag, "task edit failed");
            	showMessage(R.string.edit_task_fail);
            	break;
            case REMOVE_TASK_SUCCESS:
                Debug.d(mTag, "task removed successfully, let's update ui");
                mTaskListAdapter.removeElement((Task)msg.obj);
                break;
            case REMOVE_TASK_FAILED:
                break;
            case BACKUP_RESTORE_SUCCESS:
                Debug.d(mTag, "Backup restore done successfully");
                mTaskListAdapter.clear();
                // getting newest restored list
                mTaskListAdapter.setElements(FACADE.getTasks());
                break;
            case BACKUP_RESTORE_FAILED:
                Debug.d(mTag, "Backup restore has failed");
                showMessage(R.string.lab_data_restore_failed);
                break;
            }
        }
    };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        /*
         * (non-Javadoc)
         * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            final Task task     = intent.getParcelableExtra(TASK_KEY);
            if (Debug.isDebugable()) {
                Debug.d(mTag, "action=" + action + " task=" + task);
            }            
            if (ADD_NEW_TASK_ACTION.equals(action)) {
            	sendMsg(task == null ? 
                        ADD_NEW_TASK_FAILED : 
                        ADD_NEW_TASK_SUCCESS, 
                        task);
            } else if (EDIT_TASK_ACTION.equals(action)) {
            	sendMsg(task == null ? 
            			EDIT_TASK_FAILED : 
            			EDIT_TASK_SUCCESS, 
                        task);            	
            } else if (REMOVE_TASK_ACTION.equals(action)) {
            	sendMsg(task == null ? 
            			REMOVE_TASK_FAILED : 
            			REMOVE_TASK_SUCCESS, 
                        task);            	            	
            } else if (BACKUP_RESTORE_ACTION.equals(action)) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();    
                }
                final boolean result = intent.getBooleanExtra(RESTORE_KEY, false);
                sendMsg(result ?
                        BACKUP_RESTORE_SUCCESS :
                        BACKUP_RESTORE_FAILED,
                        null);
            }
        }        
    };
    private final BroadcastReceiver mLocaleChangedReceiver = new BroadcastReceiver() {

        /*
         * (non-Javadoc)
         * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Debug.isDebugable()) {
                Debug.d(mTag, "action=" + action);
            }            
            if (Intent.ACTION_LOCALE_CHANGED.equals(action)) {
                Debug.d(mTag, "Update tasklist description");
                // A little expensive operation but not common to user
                // go back and forth in locale change screen, it's supposed
                // to happen very rarely into the entire app lifecycle
                DateUtil.updateLocale();
                mTaskListAdapter.setElements(FACADE.getTasks());
            }
        }        
    };
	private IntentFilter mFilter;
	private IntentFilter mLocaleChangedFilter;
    
	/*
	 * (non-Javadoc)
	 * @see com.yattatech.dbtc.activity.GenericFragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main_screen);
		final int color                  = getResources().getColor(R.color.grey2);
		final ActionBar actionBar        = getActionBar();
		mFilter                          = new IntentFilter();
		mLocaleChangedFilter             = new IntentFilter();
		mTaskList                        = (DragSortListView)findViewById(R.id.taskList);
		mTaskListAdapter                 = new TaskListAdapter(this);
        final String appKey              = getString(R.string.dropbox_key);
        final String appSecret           = getString(R.string.dropbox_secret);
        final AppKeyPair pair            = new AppKeyPair(appKey, appSecret);
        final AndroidAuthSession session = new AndroidAuthSession(pair);
        mDropboxAPI                      = new DropboxAPI<AndroidAuthSession>(session);
        mToken                           = FACADE.getDropBoxToken();	
		mTaskList.setAdapter(mTaskListAdapter);
		mTaskList.setOnItemClickListener(mTaskListListener);
		mTaskList.setDropListener(mDropListener);
		mTaskList.setDragScrollProfile(mScrollProfile);
		mTaskList.setFloatViewManager(new MainViewManager(mTaskList));
		mFilter.addAction(ADD_NEW_TASK_ACTION);
		mFilter.addAction(EDIT_TASK_ACTION);
		mFilter.addAction(REMOVE_TASK_ACTION);
		mFilter.addAction(BACKUP_RESTORE_ACTION);
		mLocaleChangedFilter.addAction(Intent.ACTION_LOCALE_CHANGED);
        actionBar.setIcon(R.drawable.ic_task);
        actionBar.setTitle(R.string.app_name);
        actionBar.setBackgroundDrawable(new ColorDrawable(color));
        Broadcaster.registerLocalReceiver(mReceiver, mFilter);
        registerReceiver(mLocaleChangedReceiver, mLocaleChangedFilter);
        registerForContextMenu(mTaskList);
        final List<Task> tasks = FACADE.getTasks();
        mTaskListAdapter.setElements(FACADE.restoreDataOrder(tasks));
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;	
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
		mIndex                            = info.position;
		final Task task                   = getSelectedTask(false);
		final int menuRes                 = ((task == null) || (!task.mFinished)) ? R.menu.context_menu : R.menu.disable_context_menu; 
		getMenuInflater().inflate(menuRes, menu);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.yattatech.dbtc.activity.GenericFragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
	    super.onResume();
	    // TODO
	    // FIXME
	    // XXX
	    // It's expensive so make more tests here to see the impact on
	    // performance
	    mTaskListAdapter.notifyDataSetChanged();
	    if (mDropBoxOp == DropBoxOp.UNKNOWN) {
	    	// Workaround to avoid reentrant method whenever
	    	// user go back after has authenticated
	    	return;
	    }
	    if (mDropboxAPI.getSession().authenticationSuccessful()) {
            mDropboxAPI.getSession().finishAuthentication();
            mToken = mDropboxAPI.getSession().getOAuth2AccessToken();
            FACADE.setDropBoxToken(mToken);
            if (mDropBoxOp == DropBoxOp.SYNC) {
                Debug.d(mTag, "syncing data");
                startUpload();
            } else if (mDropBoxOp == DropBoxOp.RESTORE) {
                Debug.d(mTag, "restoring data");
                restoreContent();
            } else {
                Debug.d(mTag, "unknown state");
            }
	    }
	}
		
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (Debug.isDebugable()) {
			Debug.d(mTag, "onOptionsItemSelected=" + item);
		}		
		switch (item.getItemId()) {		
			case R.id.addItem: {
				Debug.d(mTag, "creating a new task into the chain");
				startNewActivity(NewTaskScreen.class);
				break;
			}
			case R.id.editItem: {
				Debug.d(mTag, "editing task into the chain");
				final Task task = getSelectedTask(false);
				if (task != null) {
				    startActivityWithTask(task, NewTaskScreen.class);    
				}
				break;
			}
			case R.id.removeItem: {	
			    Debug.d(mTag, "removing a task from the chain");
			    final Task task = getSelectedTask(true);
			    if (task != null) {
			        FACADE.removeTask(task);    
			    }
				break;				
			}
			case R.id.syncItem: {
				Debug.d(mTag, "calling sync screen");
				if (isConnected()) {
				    syncContent();    
				} else {
				    showMessage(R.string.lab_no_connection);
				}
				break;
			}
			case R.id.restoreItem: {
			    Debug.d(mTag, "restoring backup items");
			    if (isConnected()) {
			        restoreContent();    
			    } else {
			        showMessage(R.string.lab_no_connection);
			    }
			    break;
			}
			case R.id.logoffItem: {
			    Debug.d(mTag, "logoff from DropBox");
			    dropboxLogout();
			    break;
			}
			case R.id.creditsItem: {
			    Debug.d(mTag, "calling credits screen");
			    break;
			}
			case R.id.settingsItem: {
				Debug.d(mTag, "calling settings screen");
				break;
			}
			case R.id.exitItem: {
			    Debug.d(mTag, "exiting DBTC app");
			    finish();
			    break;
			}
			default: {
				// It's not supposed to reach here
				assert false;
			}
		}
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		mIndex                            = info.position;
		if (Debug.isDebugable()) {
			Debug.d(mTag, "onContextItemSelected item=" + item            +
					      " view="                      + info.targetView +
					      " position="                  + info.position   +
					      " id="                        + info.id);
		}		
		switch (item.getItemId()) {		
			case R.id.editItem: {
				Debug.d(mTag, "editing task into the chain");
				final Task task = getSelectedTask(false);
				if (task != null) {
				    startActivityWithTask(task, NewTaskScreen.class);    
				}
				break;
			}
			case R.id.removeItem: {	
			    Debug.d(mTag, "removing a task from the chain");
			    final Task task = getSelectedTask(true);
			    if (task != null) {
			        FACADE.removeTask(task);    
			    }
				break;				
			}
			case R.id.finishItem: {
				Debug.d(mTag, "Marking task as finished");
				final Task task = getSelectedTask(true);
				if (!task.mFinished) {
					task.mFinished = true;
					FACADE.editTask(task);
				}
				break;
			}
			default: {
				// It's not supposed to reach here
				assert false;
			}
		}
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.yattatech.dbtc.activity.GenericFragmentActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Broadcaster.unregisterLocalReceiver(mReceiver);
		unregisterReceiver(mLocaleChangedReceiver);
	}
	
	/*
     * (non-Javadoc)
     * @see com.yattatech.dbtc.activity.GenericFragmentActivity#onBackPressed()
     */
	@Override
	public void onBackPressed() {
	    FACADE.saveDataOrder(mTaskListAdapter.getSource());
	    super.onBackPressed();
	}
	
	public boolean isTaskChecked(Task task) {
	    return FACADE.isTaskChecked(task);
	}
	
	public int getChecksCountByTask(final Task task) {
		return FACADE.getChecksCountByTask(task);
	}
	
	private Task getSelectedTask(boolean remove) {
	    Task task = null;
	    if (mTaskListAdapter.hasElements()) {
	        task = mTaskListAdapter.getItem(mIndex);	        
	        if (remove) {
	            final int count = mTaskListAdapter.getCount() - 1;   // pretend to act as object has been already removed
	            if (count == 0) {                                    // nothing to do
	                Debug.d(mTag, "TaskList empty");
	            } else if (count <= mIndex) {                        // last index element 
	                mIndex = count - 1;
	            } else {                                             // nothing to do neither
	            	Debug.d(mTag, "Index is between 0 and maxSize");
	            }
	        }
	    }
	    return task;
	}
	
	private void dropboxLogout() {
	    FACADE.dropboxLogout();
	    mToken = null;
	    mDropboxAPI.getSession().unlink();
	}
	
	private void startActivityWithTask(final Task task, Class<?> clazz) {
        final Intent intent = new Intent(this, clazz);
        intent.putExtra(Constants.TASK_KEY, (Parcelable)task);
        startActivity(intent);
	}
	
	private void sendMsg(final int what, final Task task) {		
        final Message msg = mH.obtainMessage(what,
                                             task);
        mH.sendMessage(msg);
	}
		
	private void syncContent() {
	    mDropBoxOp = DropBoxOp.SYNC;
        if (mDropboxAPI.getSession().isLinked()) {
            Debug.d(mTag, "DropBox already linked");
            startUpload();
        } else {
            mToken = FACADE.getDropBoxToken();
            if (StringUtils.isEmpty(mToken)) {
                mDropboxAPI.getSession().startOAuth2Authentication(this);
            } else {
                mDropboxAPI.getSession().setOAuth2AccessToken(mToken);
                startUpload();
            }
        } 
	}
	
	private void restoreContent() {
	    mDropBoxOp = DropBoxOp.RESTORE;
        if (mDropboxAPI.getSession().isLinked()) {
            Debug.d(mTag, "DropBox already linked");
            startRestore();
        } else {
            mToken = FACADE.getDropBoxToken();
            if (StringUtils.isEmpty(mToken)) {
                mDropboxAPI.getSession().startOAuth2Authentication(this);
            } else {
                mDropboxAPI.getSession().setOAuth2AccessToken(mToken);
                startRestore();
            }
        } 	    
	}
	
	private void startUpload() {
	    mDropBoxOp = DropBoxOp.UNKNOWN;
		if (!isConnected()) {
			showMessage(R.string.lab_no_connection);
		} else if (FACADE.hasTasks()) {
		    cancelAsyncTask(mUploadBackupAsyncTask);
		    mUploadBackupAsyncTask = new UploadBackupAsyncTask();
		    mUploadBackupAsyncTask.execute();			
		} else {
			Debug.d(mTag, "Has no task to be sync");
			showMessage(R.string.lab_no_data_sync);
		}
	}
	
	private void startRestoreAsync() {
        cancelAsyncTask(mRestoreBackupAsyncTask);
        mRestoreBackupAsyncTask = new RestoreBackupAsyncTask();
        mRestoreBackupAsyncTask.execute();		
	}
	
	private void startRestore() {
	    mDropBoxOp = DropBoxOp.UNKNOWN;
		if (!isConnected()) {
			showMessage(R.string.lab_no_connection);		
		} else if (FACADE.hasTasks()) {
			showRestoreMessage();
		} else {
			Debug.d(mTag, "No data to be lost, restore anyway");
			startRestoreAsync();
		}		
	}
		
	private void showRestoreMessage() {
	    new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
        			   .setTitle(R.string.lab_confirm)
        			   .setMessage(R.string.lab_data_lost)
        			   .setCancelable(false)
        			   .setPositiveButton(R.string.lab_yes, new DialogInterface.OnClickListener() {
            
				            /*
				             * (non-Javadoc)
				             * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
				             */
				            @Override
				            public void onClick(DialogInterface dialog, int buttonId) {
				            	startRestoreAsync();
				            }
				        })
				        .setNegativeButton(R.string.lab_no,  new DialogInterface.OnClickListener() {
				            
				            /*
				             * (non-Javadoc)
				             * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
				             */
				            @Override
				            public void onClick(DialogInterface dialog, int buttonId) {
				            }
				        })
				        .create()
				        .show();	    		
	}
	
	private final class RestoreBackupAsyncTask extends AsyncTask<Void, Long, List<Task>> {
	    
		private String mDropboxErrorMsg;
		
        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            showSpinnerProgressBar(R.string.lab_restore_title);
        }
	        
        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
         */
        @Override
        protected List<Task> doInBackground(Void... params) {
            DropboxInputStream in = null;
            try {
                in          = mDropboxAPI.getFileStream(Constants.BACKUP_FILE_NAME, null);
                String json = IOUtils.toString(in, "UTF-8");
                return FACADE.getTasksFromJson(json);
            } catch (DropboxServerException e) {            	
            	Debug.e(mTag, "Failed:", e);
            	Debug.e(mTag, "parsedResponse" + e.parsedResponse);
            	mDropboxErrorMsg = (String)e.parsedResponse.get("error");
            } catch (Exception e) {
                Debug.e(mTag, "Failed:", e);
            } finally {
                IOUtils.closeQuietly(in);
            }
            return null;
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(List<Task> result) {
        	if (mDropboxErrorMsg != null) {
        		mProgressDialog.dismiss();
        		showMessage(R.string.backup_fail_detail, mDropboxErrorMsg);
        	} else if (result == null) {
                mProgressDialog.dismiss();
                showMessage(R.string.lab_data_restore_failed);
            } else if (result.isEmpty()) {
            	mProgressDialog.dismiss();
            	showMessage(R.string.lab_no_data_restore);
            } else {
                FACADE.restoreTasks(result);
            }
        }
	}
	
	private final class UploadBackupAsyncTask extends AsyncTask<Void, Void, Entry> {
	    
	    /*
	     * (non-Javadoc)
	     * @see android.os.AsyncTask#onPreExecute()
	     */
	    @Override
	    protected void onPreExecute() {
	    	showSpinnerProgressBar(R.string.lab_sync_title);
	    }
	    
	    /*
	     * (non-Javadoc)
	     * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
	     */
        @Override
        protected Entry doInBackground(Void... params) {
            Entry entry          = null;
            BackupInputStream in = null;
            try {
                in    = FACADE.getBackupInputStream();
                entry = mDropboxAPI.putFileOverwrite(Constants.BACKUP_FILE_NAME, in, in.mLength, null);
            } catch (Exception e) {
                Debug.e(mTag, "Failed:", e);
            } finally {
                IOUtils.closeQuietly(in);
            }
            return entry;
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Entry result) {
            mProgressDialog.dismiss();
            if (result == null) {
                showMessage(R.string.backup_fail);
            } else {
                showMessage(R.string.backup_success);
            }
        }
	}	
	
	private final class MainViewManager implements DragSortListView.FloatViewManager {

	    private Bitmap mFloatBitmap;
	    private ImageView mImageView;
	    private ViewGroup.LayoutParams mLayoutParams;
	    private final ListView mListView;

	    public MainViewManager(ListView listView) {
	        mListView = listView;
	    }
	    
	    /*
	     * (non-Javadoc)
	     * @see com.mobeta.android.dslv.DragSortListView.FloatViewManager#onCreateFloatView(int)
	     */
	    @Override
	    public View onCreateFloatView(int position) {
	        final View view = mListView.getChildAt(position + mListView.getHeaderViewsCount() - mListView.getFirstVisiblePosition());
	        if (view == null) {
	            return null;
	        }
	        view.setPressed(false);

	        // Create a copy of the drawing cache so that it does not get
	        // recycled by the framework when the list tries to clean up memory
	        //v.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
	        view.setDrawingCacheEnabled(true);
	        mFloatBitmap = Bitmap.createBitmap(view.getDrawingCache());
	        view.setDrawingCacheEnabled(false);
	        if (mImageView == null) {
	            mImageView = new ImageView(mListView.getContext());
	        }
	        if (mLayoutParams == null) {
	            mLayoutParams = new ViewGroup.LayoutParams(view.getWidth(), view.getHeight());
	        }
	        mImageView.setImageBitmap(mFloatBitmap);
	        mImageView.setLayoutParams(mLayoutParams);
	        return mImageView;
	    }
	    
	    /*
	     * (non-Javadoc)
	     * @see com.mobeta.android.dslv.DragSortListView.FloatViewManager#onDragFloatView(android.view.View, android.graphics.Point, android.graphics.Point)
	     */
	    @Override
	    public void onDragFloatView(View floatView, Point position, Point touch) {
	    }
	    
	    /*
	     * (non-Javadoc)
	     * @see com.mobeta.android.dslv.DragSortListView.FloatViewManager#onDestroyFloatView(android.view.View)
	     */
	    @Override
	    public void onDestroyFloatView(View floatView) {
	        ((ImageView)floatView).setImageDrawable(null);
	        mFloatBitmap.recycle();
	        mFloatBitmap = null;
	    }
	}
}
