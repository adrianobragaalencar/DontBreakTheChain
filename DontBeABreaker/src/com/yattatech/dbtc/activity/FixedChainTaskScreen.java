/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.activity;

import java.util.Calendar;

import android.os.Bundle;

import com.yattatech.dbtc.adapter.FixedCalendarAdapter;
import com.yattatech.dbtc.domain.InternalDate;
import com.yattatech.dbtc.domain.Task;
import com.yattatech.dbtc.log.Debug;

/**
 * Screen that represents the calendar chain per {@link Task}
 * defined 
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class FixedChainTaskScreen extends AbstractChainTaskScreen {

    /*
     * (non-Javadoc)
     * @see com.yattatech.dbtc.activity.GenericFragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        if (!mTask.mDefined) {
            Debug.d(mTag, "Task must be a defined one");
            throw new IllegalStateException("FixedChainTaskScreen cannot be called with undefined task");
        }
        mCalendarAdapter = new FixedCalendarAdapter(this, mTask);
        mGridView.setAdapter(mCalendarAdapter);        
    }
	
	/*
	 * (non-Javadoc)
	 * @see com.yattatech.dbtc.activity.AbstractChainTaskScreen#onLeftClicked()
	 */
	@Override
	protected void onLeftClicked() {
		final InternalDate date = FixedCalendarAdapter.BEGIN_DATE;
        final int month         = mMonth.get(Calendar.MONTH);
        final int year          = mMonth.get(Calendar.YEAR);
        if (((month - 1) < date.mMonth) && 
            ((year       < date.mYear) ||
             (year - 1   < date.mYear))) {
            // We can't move calendar cursor to out of minimun
            // date bounds
            Debug.d(mTag, "Min Date Bounds reached");
            return;
        }
        if (month == mMonth.getActualMinimum(Calendar.MONTH)) {                
            mMonth.set(year - 1, mMonth.getActualMaximum(Calendar.MONTH), 1);
        } else {
            mMonth.set(Calendar.MONTH, month - 1);
        }
        refreshCalendar();        
	}

	/*
	 * (non-Javadoc)
	 * @see com.yattatech.dbtc.activity.AbstractChainTaskScreen#onRightClicked()
	 */
	@Override
	protected void onRightClicked() {
        final InternalDate date = FixedCalendarAdapter.END_DATE;
        final int minMonth      = mMonth.getActualMinimum(Calendar.MONTH);
        final int month         = mMonth.get(Calendar.MONTH);
        final int year          = mMonth.get(Calendar.YEAR);        
        if (month == mMonth.getActualMaximum(Calendar.MONTH)) {   
            if (year + 1 > date.mYear) {
              Debug.d(mTag, "Current Date Bounds reached");
              return;                
            }
            mMonth.set(year + 1, minMonth, 1);
        } else {
            if (((month + 1) >  date.mMonth) &&
                 (year       >= date.mYear)) {
                Debug.d(mTag, "Current Date Bounds reached");
                return;                                
            } 
            mMonth.set(Calendar.MONTH, month + 1);
        }
        refreshCalendar();        
	}
}
