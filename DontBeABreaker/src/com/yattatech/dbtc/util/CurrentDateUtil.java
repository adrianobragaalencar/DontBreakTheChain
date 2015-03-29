/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.util;

import java.util.Calendar;
import com.yattatech.dbtc.domain.InternalDate;

/**
 * Utility class which acts only a placeholder for
 * current date
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class CurrentDateUtil {

    private static final Calendar CALENDAR = Calendar.getInstance();
    private static final Object LOCK       = new Object();
	public static InternalDate sCurrentDate;
	
	private CurrentDateUtil() {
		throw new AssertionError();
	}	
	
    public static void updateCurrentDate() {
        // Update month calendar to correctly get
        // current day
        CALENDAR.setTimeInMillis(System.currentTimeMillis());
        synchronized (CurrentDateUtil.LOCK) {
            final InternalDate previusDay = sCurrentDate;
            final InternalDate currentDay = new InternalDate();        
            currentDay.mDay               = CALENDAR.get(Calendar.DAY_OF_MONTH);
            currentDay.mMonth             = CALENDAR.get(Calendar.MONTH);
            currentDay.mYear              = CALENDAR.get(Calendar.YEAR);  
            if (previusDay != null) {
                if (currentDay.isGreaterThanCurrentDay(previusDay)) {
                    sCurrentDate = currentDay;
                } else {
                    // it means the user has change its system clock
                    // after app has been started.
                }
            } else {
                // run at very first time so we don't have yet
                // any date set
               sCurrentDate = currentDay;
            }            
        }
    }
}
