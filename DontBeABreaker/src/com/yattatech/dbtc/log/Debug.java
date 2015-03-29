/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.log;

import com.yattatech.dbtc.BuildConfig;

import android.util.Log;

/**
 * Utility debug class that only logs some message when
 * we are running in a debug mode, it's very useful to
 * avoid unneeded IO access in release mode
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class Debug {

	private Debug() {
		throw new AssertionError();
	}
			
	public static void e(final String tag, final String msg) {
		if (BuildConfig.DEBUG) {
			Log.e(tag, msg);
		}
	}
	
	public static void e(final String tag, final String msg, final Throwable t) {
		if (BuildConfig.DEBUG) {
			Log.e(tag, msg, t);
		}
	}

	public static void d(final String tag, final String msg) {
		if (BuildConfig.DEBUG) {
			Log.d(tag, msg);
		}
	}
	
	public static void d(final String tag, final String msg, final Throwable t) {
		if (BuildConfig.DEBUG) {
			Log.d(tag, msg, t);
		}
	}

	public static void v(final String tag, final String msg) {
		if (BuildConfig.DEBUG) {
			Log.v(tag, msg);
		}
	}
	
	public static void v(final String tag, final String msg, final Throwable t) {
		if (BuildConfig.DEBUG) {
		    Log.v(tag, msg, t);
		}
	}

	public static void i(final String tag, final String msg) {
	    if (BuildConfig.DEBUG) {
		    Log.i(tag, msg);
		}
	}
	
	public static void i(final String tag, final String msg, final Throwable t) {
	    if (BuildConfig.DEBUG) {
	        Log.i(tag, msg, t);
	    }
	}
	
	public static boolean isDebugable() {
		return BuildConfig.DEBUG;		
	}
}
