/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.bg;

import java.util.concurrent.TimeUnit;

import com.yattatech.dbtc.log.Debug;
import com.yattatech.dbtc.util.CurrentDateUtil;

/**
 * Thread responsible for syncing current date 
 * created as soon as app started, it's totally
 * needed to avoid user start app and take it 
 * awake more than 24 hours and won't be able to
 * check new date in calendar, cause current date
 * will be out of date. To try to minimize any 
 * problem that class is supposed to run every one
 * or so.
 * That {@link Thread} must be created as soon as
 * the app has been started and only be killed when
 * the app is being killing.
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class ClockSyncThread implements Runnable {

    private static final String TAG = "ClockSyncThread";
	private final Thread mWork;
	private final long mTimeout;
	private volatile boolean mRunning;
	
	public ClockSyncThread() {
		mTimeout  = 1L;
		mRunning  = false;
		mWork     = new Thread(this, "ClockSyncThread-0");
		mWork.setDaemon(true);
		mWork.setPriority(Thread.MIN_PRIORITY);
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    
		    /*
		     * (non-Javadoc)
		     * @see java.lang.Thread#run()
		     */
		    @Override
		    public void run() {
		    	ClockSyncThread.this.stop();
		    }
		});
	}
	
	public void start() {
		if (mRunning) {
			throw new IllegalStateException("ClockSyncThread has been started yet");			
		}
		if (Debug.isDebugable()) {
		    Debug.d(TAG, "starting " + mWork.getName());
		}
		mRunning = true; 
		mWork.start();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		runForever();
	}
	
	private void runForever() {
		final long timeout = mTimeout;
		for (;;) {
			if (!mRunning) {
				break;
			}
		    Debug.d(TAG, "runForever CurrentDateUtil.updateCurrentDate()");
			CurrentDateUtil.updateCurrentDate();
			try {
			    Debug.d(TAG, "Sleeping for while");
				TimeUnit.HOURS.sleep(timeout);
			} catch (InterruptedException e) {
			}
		}
	}
		
	public void stop() {
	    if (Debug.isDebugable()) {
	        Debug.d(TAG, "stoping " + mWork.getName());
	    }
		mRunning = false;
	}
}
