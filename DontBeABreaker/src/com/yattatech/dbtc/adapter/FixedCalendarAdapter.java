/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.adapter;

import java.util.Calendar;

import android.view.View;
import android.view.ViewGroup;

import com.yattatech.dbtc.R;
import com.yattatech.dbtc.activity.AbstractChainTaskScreen;
import com.yattatech.dbtc.domain.InternalDate;
import com.yattatech.dbtc.domain.Task;

/**
 * {@link CalendarAdapter} that represents a fixed one
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class FixedCalendarAdapter extends CalendarAdapter {
	
	public static final InternalDate BEGIN_DATE = new InternalDate();
	public static final InternalDate END_DATE   = new InternalDate();
    private final int mBeginColor;
    private final int mEndColor;
	
    public FixedCalendarAdapter(AbstractChainTaskScreen context, Task task) {
    	super(context);
    	final Calendar c  = Calendar.getInstance();
    	c.set(task.mYearCreation, task.mMonthCreation, task.mDayCreation);
    	c.add(Calendar.DAY_OF_MONTH, task.mMaxDays - 1);    // current day as offset
    	BEGIN_DATE.mDay   = task.mDayCreation;
    	BEGIN_DATE.mMonth = task.mMonthCreation;
    	BEGIN_DATE.mYear  = task.mYearCreation;    	    	
    	END_DATE.mDay     = c.get(Calendar.DAY_OF_MONTH);
    	END_DATE.mMonth   = c.get(Calendar.MONTH);
    	END_DATE.mYear    = c.get(Calendar.YEAR);
    	mBeginColor       = mContext.getResources().getColor(R.color.green);
    	mEndColor         = mContext.getResources().getColor(R.color.red);
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	super.getView(position, convertView, parent);
        final InternalDate date = getItem(position);  
        if ((date == null) || (date == InternalDate.DUMMY)) {
            mDayView.setClickable(false);
            mDayView.setFocusable(false);
            return mView;
        } 
        if ((date.isLessThanCurrentDay(BEGIN_DATE)) ||
        	(date.isGreaterThanCurrentDay(END_DATE))) {
            mDayView.setClickable(false);
            mDayView.setFocusable(false);            
            mDayView.setTextColor(mGrayColor);            
        } else {
        	if (date.isEqualsTo(BEGIN_DATE)) {
        		mDayView.setTextColor(mBeginColor);
        	} else if (date.isEqualsTo(END_DATE)) {
        		mDayView.setTextColor(mEndColor);
        	}        
        	if (contains(date)) {
        		mImgCheck.setVisibility(View.VISIBLE);
        	}
        	mView.setOnClickListener(mViewClickListener);    
        	mView.setTag(date);        	        
        }
        mDayView.setText(date.getDay());
        return mView;        
    }    
}
