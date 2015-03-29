/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc;

import com.yattatech.dbtc.bg.ClockSyncThread;
import com.yattatech.dbtc.log.Debug;

import android.app.Application;
import android.content.Context;

/**
 * Application that maintains a global application state. 
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com) 
 * 
 */
public final class DBTCApplication extends Application {
    
    private static final String TAG = "DBTCApplication";
	public static Context sApplicationContext;
	private static final ClockSyncThread CLOCK_SYNCER = new ClockSyncThread();
	
	public DBTCApplication() {
	    if (Debug.isDebugable()) {
	        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
	            
	            /*
	             * (non-Javadoc)
	             * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
	             */
	            @Override
	            public void uncaughtException(Thread thread, Throwable t) {
	                Debug.d(TAG, "Thread=" + thread + " Throwable=" + t);
	            }
	        });	        
	    }
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
    @Override
    public void onCreate() {
        super.onCreate();
        if (sApplicationContext == null) {
        	sApplicationContext = getApplicationContext();
        }
        CLOCK_SYNCER.start();
    }
    
    /*
     * (non-Javadoc)
     * @see android.app.Application#onLowMemory()
     */
    @Override
    public void onLowMemory() {
        if (Debug.isDebugable()) {
            Debug.d(TAG, "onLowMemory");
        }
        super.onLowMemory();
    }
}
