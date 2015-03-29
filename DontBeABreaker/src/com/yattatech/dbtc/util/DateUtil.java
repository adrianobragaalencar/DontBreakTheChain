/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.util;

import java.text.DateFormatSymbols;
import java.util.Locale;

import android.content.Context;

import com.yattatech.dbtc.DBTCApplication;
import com.yattatech.dbtc.R;
import com.yattatech.dbtc.log.Debug;

/**
 * Utility class which takes a format and date and
 * translate it to a printable/readable format
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class DateUtil {
	
	private static final String TAG           = "DateUtil";
	private static final StringBuilder BUFFER = new StringBuilder(10);
	private static String DAY_P               = "dd";
	private static String MONTH_P             = "MM";
	private static String sFormat;
	private static String[] sParts;
	private static String[] sDayDesc;

	private DateUtil() {
		throw new AssertionError();
	}
	
	/**
	 * Whenever the user has changed his/her locale configuration
	 * that method will be called by android updating date format
	 * to be exactly as expected as current locale value
	 * 
	 */
	public static void updateLocale() {
		sFormat  = getFormat();
		sParts   = sFormat.split("/");
		sDayDesc = null;
	}
	
	public static String[] getDaysDesc() {
		Debug.d(TAG, "getDaysDesc");
		if (sDayDesc != null) {
			return sDayDesc;
		}
        final Context context = DBTCApplication.sApplicationContext;
        Locale current        = context.getResources().getConfiguration().locale;
        if (current == null) {
            current           = Locale.US;
        }       
        if (Debug.isDebugable()) {
            Debug.d(TAG, "currentLocale=" + current);
        }        
        sDayDesc = new DateFormatSymbols(current).getShortWeekdays();
        return sDayDesc;
	}
	
	public static String format(int year, int month, int day) {
		if (sFormat == null) {
			sFormat = getFormat();
			sParts  = sFormat.split("/");
		}		
		final StringBuilder buffer = BUFFER;
		final int num0             = getValidNumber(year, month, day, sParts[0]);
		final int num1             = getValidNumber(year, month, day, sParts[1]);
		final int num2             = getValidNumber(year, month, day, sParts[2]);
		if (num0 <= 9) buffer.append('0').append(num0);
		else           buffer.append(num0);
		buffer.append('/');
		if (num1 <= 9) buffer.append('0').append(num1);
		else           buffer.append(num1);
		buffer.append('/');
		if (num2 <= 9) buffer.append('0').append(num2);
		else           buffer.append(num2);
		final String date = buffer.toString();
		buffer.setLength(0);
		return date;
	}
	
	private static int getValidNumber(int year, int month, int day, String part) {
		if (DAY_P.equals(part)) {
			return day;
		} else if (MONTH_P.equals(part)) {
			return month;
		} else {  //YEAR_P.equals(paart)    
			return year;
		}		
	}
				
	private static String getFormat() {
		return DBTCApplication.sApplicationContext.getString(R.string.date_format);
	}	
}
