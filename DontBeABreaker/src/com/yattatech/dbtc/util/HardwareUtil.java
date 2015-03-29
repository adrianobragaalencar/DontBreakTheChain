/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.util;

import com.yattatech.dbtc.DBTCApplication;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Utility class responsible for detecting
 * some kind of features based in current 
 * running device
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class HardwareUtil {

	public static final boolean SMALL_LAYOUT;
	public static final boolean IS_TABLET;	
	private static final ConnectivityManager CONNECTIVITY_MANAGER;
	
	static {
		final Context context    = DBTCApplication.sApplicationContext;
		final Configuration conf = context.getResources().getConfiguration();
		final int screenLayout   = conf.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		SMALL_LAYOUT             = (screenLayout == Configuration.SCREENLAYOUT_SIZE_SMALL);
	    boolean large            = (screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE);
	    boolean xlarge           = (screenLayout == Configuration.SCREENLAYOUT_SIZE_XLARGE);
		IS_TABLET                = (large) || (xlarge);		
		CONNECTIVITY_MANAGER     = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	private HardwareUtil() {
		throw new AssertionError();
	}
	
	/**
	 * Returns the connection type currently been used, it could
	 * be wi-fi, wi-fi direct, 3g or something else
	 *  
	 * @return String 
	 * 
	 */
	public static String getConnectionType() {
        final NetworkInfo info = CONNECTIVITY_MANAGER.getActiveNetworkInfo();
        if ((info != null) && (info.isConnected())) {
        	final String type    = info.getTypeName();
        	final String subType = info.getSubtypeName();
        	if (!StringUtils.isEmpty(subType)) {
        		return type + "-" + subType; 
        	}
        	return type;
        }
        return "UNKNOWN";
	}	
}
