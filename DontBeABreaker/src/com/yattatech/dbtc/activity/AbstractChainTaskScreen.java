/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.activity;

import static com.yattatech.dbtc.Constants.TWITTER_LOGIN_KEY;
import static com.yattatech.dbtc.activity.AsyncMessages.TWITTER_LOGIN_ACTION;

import java.util.Calendar;
import java.util.List;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.GridView;
import android.widget.TextView;

import com.yattatech.dbtc.Constants;
import com.yattatech.dbtc.R;
import com.yattatech.dbtc.activity.async.TwitterAuthAsyncTask;
import com.yattatech.dbtc.adapter.CalendarAdapter;
import com.yattatech.dbtc.adapter.CalendarDayAdapter;
import com.yattatech.dbtc.domain.Check;
import com.yattatech.dbtc.domain.Task;
import com.yattatech.dbtc.domain.TwitterLoginStatus;
import com.yattatech.dbtc.log.Debug;
import com.yattatech.dbtc.receiver.Broadcaster;
import com.yattatech.dbtc.util.CurrentDateUtil;
import com.yattatech.dbtc.util.HardwareUtil;

/**
 * Abstract class to be used by both calender view class
 * fixed and unlimited
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public abstract class AbstractChainTaskScreen extends GenericFragmentActivity {
    
	protected Task mTask;	
	protected Calendar mMonth;	   
	protected CalendarDayAdapter mCalendarDayAdapter;	
	protected GridView mGridViewDay;
	protected GridView mGridView;
	protected TextView mTxDateTitle;
	protected View mTxLeftIcon;
	protected View mTxRightIcon;
	protected TextView mImgTwitter;
	protected TextView mTxTaskname;
	protected TwitterAuthAsyncTask mTwitterAuthAsyncTask;
	protected CalendarAdapter mCalendarAdapter;
    private final OnClickListener mArrowsClickListener = new OnClickListener() {

        /*
         * (non-Javadoc)
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        @Override
        public void onClick(View view) {
            if (view == mTxLeftIcon) {
                onLeftClicked();
            } else if (view == mTxRightIcon) {
                onRightClicked();
            }
        }        
    };
    protected final OnClickListener mTwitterClickListener = new OnClickListener() {

    	/*
    	 * (non-Javadoc)
    	 * @see android.view.View.OnClickListener#onClick(android.view.View)
    	 */
		@Override
		public void onClick(View v) {
			cancelAsyncTask(mTwitterAuthAsyncTask);
			mTwitterAuthAsyncTask = new TwitterAuthAsyncTask(AbstractChainTaskScreen.this);
			mTwitterAuthAsyncTask.execute();
		}
	};
	protected final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        /*
         * (non-Javadoc)
         * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action             = intent.getAction();
            final TwitterLoginStatus status = intent.getParcelableExtra(TWITTER_LOGIN_KEY);
            if (Debug.isDebugable()) {
                Debug.d(mTag, "action=" + action + " status=" + status);
            }            
            if (TWITTER_LOGIN_ACTION.equals(action)) {
                switch (status) {
                case SUCCESS:  
                    Debug.d(mTag, "twitter login success");
                    break;
                case FAILED:          
                    Debug.d(mTag, "twitter login failed");
                    showMessage(R.string.lab_twitter_failed);
                    break;
                case DENIED:          
                    Debug.d(mTag, "twitter login denied");
                    showMessage(R.string.lab_twitter_denied);
                    break;
                case UNKNOWN:       
                    Debug.d(mTag, "unknown reason");
                    showMessage(R.string.lab_twitter_unknow);
                    break;                    
                default:
                    break;
                }
            } 
        }        
    };    
    protected final BroadcastReceiver mLocaleChangedReceiver = new BroadcastReceiver() {

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
                Debug.d(mTag, "Update days description");
                if (mCalendarDayAdapter != null) {
                    mCalendarDayAdapter.updateDaysDesc();
                }
            }
        }        
    };
    protected IntentFilter mFilter;
    protected IntentFilter mLocaleChangedFilter;
    
    /*
     * (non-Javadoc)
     * @see com.yattatech.dbtc.activity.GenericFragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	final boolean smallLayout = HardwareUtil.SMALL_LAYOUT; 
    	if (smallLayout) {
    		Debug.d(mTag, "Running on a very tiny small piece of hardware, let's put ActionBar away");
    		requestWindowFeature(Window.FEATURE_NO_TITLE);
    	}    	        
        setContentView(R.layout.layout_chaintask_screen);
        mMonth               = Calendar.getInstance();
        mFilter              = new IntentFilter();
        mLocaleChangedFilter = new IntentFilter();        
        mCalendarDayAdapter  = new CalendarDayAdapter();        
        mGridViewDay         = (GridView)findViewById(R.id.gridviewDay);
        mGridView            = (GridView)findViewById(R.id.gridview);
        mTxDateTitle         = (TextView)findViewById(R.id.dateTitle);
        mTxLeftIcon          = findViewById(R.id.previous);
        mTxRightIcon         = findViewById(R.id.next);     
        mImgTwitter          = (TextView)findViewById(R.id.imgTwitter);
        mTxTaskname          = (TextView)findViewById(R.id.txTaskname);
        final Bundle bundle  = getIntent().getExtras();
        mTask                = (Task)((bundle == null) ? null : bundle.getParcelable(Constants.TASK_KEY));
        if (mTask == null) {
            Debug.d(mTag, "Task must be supplied");
            throw new IllegalStateException(getClass().getSimpleName() + " cannot be called with Task null");
        }       
        mTxTaskname.setText(mTask.mName);
        mImgTwitter.setOnClickListener(mTwitterClickListener);
        mGridViewDay.setAdapter(mCalendarDayAdapter);        
        mTxLeftIcon.setOnClickListener(mArrowsClickListener);
        mTxRightIcon.setOnClickListener(mArrowsClickListener);
        mFilter.addAction(TWITTER_LOGIN_ACTION);
        mLocaleChangedFilter.addAction(Intent.ACTION_LOCALE_CHANGED);
        if (!smallLayout) {
        	setActionBar();	
        }        
        CurrentDateUtil.updateCurrentDate();
        registerReceiver(mLocaleChangedReceiver, mLocaleChangedFilter);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == Constants.TWITTER_SUCCCESS) {
    		Debug.d(mTag, "callback from twitter");
			if (data != null) {
				final Bundle bundle = data.getExtras();
				if (bundle != null) {					
					FACADE.setTwitterData(bundle);
				}
			}
    	}
    }
    
    /*
     * (non-Javadoc)
     * @see com.yattatech.dbtc.activity.GenericFragmentActivity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        Broadcaster.registerLocalReceiver(mReceiver, mFilter);        
        refreshCalendar();
    }

    /*
     * (non-Javadoc)
     * @see com.yattatech.dbtc.activity.GenericFragmentActivity#onPause()
     */
    @Override
    protected void onPause() {        
        super.onPause();
        Broadcaster.unregisterLocalReceiver(mReceiver);   
        // Whenever we go away from that class we've got 
        // to release all temporary files, cause if we 
        // don't that we going to crash as soon as start
        // another calendar adapter, cause we've got dangling
        // references into previous instance and we are fucked
        // up cause there's no way to get it back
        mCalendarAdapter.releaseTmpObjs();        
    }
    
    /*
     * (non-Javadoc)
     * @see com.yattatech.dbtc.activity.GenericFragmentActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	unregisterReceiver(mLocaleChangedReceiver);
    }
    
    public void check(int year, int month, int day) {
        if (Debug.isDebugable()) {
            Debug.d(mTag, "check year=" + year  + 
                          " month="     + month +
                          " day="       + day); 
        }        
        FACADE.saveCheck(year, month, day, mTask.mId);
    }
    
    public void uncheck(int year, int month, int day) {
        if (Debug.isDebugable()) {
            Debug.d(mTag, "uncheck year=" + year  + 
                          " month="       + month +
                          " day="         + day); 
        }       
        FACADE.removeCheck(year, month, day, mTask.mId);
    }
    
    public boolean isTaskFinished() {
    	return (mTask != null) && (mTask.mFinished);
    }
    
    protected abstract void onLeftClicked();    
    protected abstract void onRightClicked();
    
	protected void refreshCalendar() {
		loadChecks();
	    mCalendarAdapter.refreshDays(mMonth);
	    mCalendarAdapter.notifyDataSetChanged();             
	    mTxDateTitle.setText(android.text.format.DateFormat.format(Constants.DATE_TITLE_FORMAT, mMonth));
	}    
	
    private void loadChecks() {
    	final List<Check> checks = FACADE.getChecksByTaskAndDate(mTask, mMonth);
    	if ((Debug.isDebugable()) && (checks != null)) {
			Debug.d(mTag, "check size=" + checks.size() + 
					      " content="   + checks);	
    	}
    	mCalendarAdapter.setChecks(checks);
    }    
            
    protected void setActionBar() {    	    	
    	final int color            = getResources().getColor(R.color.grey2);
    	final ActionBar actionBar  = getActionBar();
        actionBar.setIcon(R.drawable.ic_calen);
        actionBar.setTitle(R.string.app_name);
        actionBar.setBackgroundDrawable(new ColorDrawable(color));
    }    
}
