/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yattatech.dbtc.R;
import com.yattatech.dbtc.activity.AbstractChainTaskScreen;
import com.yattatech.dbtc.adt.TPool;
import com.yattatech.dbtc.domain.Check;
import com.yattatech.dbtc.domain.InternalDate;
import com.yattatech.dbtc.log.Debug;
import com.yattatech.dbtc.util.StringUtils;

/**
 * Adapter responsible for creating calendar cells
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public abstract class CalendarAdapter extends GenericAdapter<InternalDate> {	
 		
	private static final Check DUMMY_SEARCH = new Check();
    protected final AbstractChainTaskScreen mContext;    
    protected List<Check> mChecks;
    protected final int mBlackColor;
    protected final int mGrayColor;
    private final String mTag;
    protected View mView;
    protected TextView mDayView;  
    protected ImageView mImgCheck;        
    protected static final TPool<InternalDate> POOL = new TPool<InternalDate>(31, new TPool.TPoolAllocator<InternalDate>() {

        /*
         * (non-Javadoc)
         * @see com.yattatech.dbtc.adt.TPool.TPoolAllocator#allocate()
         */
        @Override
        public InternalDate allocate() {
            return new InternalDate();
        }        
    });
    
    protected final OnClickListener mViewClickListener = new OnClickListener() {
        
        /*
         * (non-Javadoc)
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        @Override
        public void onClick(View view) {
        	if (mContext.isTaskFinished()) {
        		Debug.d(mTag, "Task finished readonly");        		
        		return;
        	}
        	
            final ImageView dayViewCheck = (ImageView)view.findViewById(R.id.dateChecked);
            final InternalDate date      = (InternalDate)view.getTag();
            final int year               = date.mYear;
            final int month              = date.mMonth;
            final int day                = date.mDay;
            if (dayViewCheck.getVisibility() == View.VISIBLE) {
                dayViewCheck.setVisibility(View.INVISIBLE);
                mContext.uncheck(year, month, day);                
            } else {
                dayViewCheck.setVisibility(View.VISIBLE);
                mContext.check(year, month, day);
            }
        }
    };
    
    public CalendarAdapter(AbstractChainTaskScreen context) {    
        mContext    = context;
        mTag        = getClass().getSimpleName();
        mSource     = new ArrayList<InternalDate>();                
        mBlackColor = mContext.getResources().getColor(R.color.black);
        mGrayColor  = mContext.getResources().getColor(R.color.grey2);
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	mView                    = convertView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            LayoutInflater li    = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView                = li.inflate(R.layout.calendar_item, null);            
        }
        mDayView                 = (TextView)mView.findViewById(R.id.date);  
        mImgCheck                = (ImageView)mView.findViewById(R.id.dateChecked);        
        // Reset previous elements created and set by any 
        // previous calendar view
        mView.setTag(null);
        mDayView.setTextColor(mBlackColor);
        mDayView.setText(StringUtils.EMPTY_STR);
        mImgCheck.setVisibility(View.INVISIBLE);
        mView.setOnClickListener(null);
        return mView;
    }    
    
    /*
     * (non-Javadoc)
     * @see com.yattatech.dbtc.adapter.GenericAdapter#addElement(java.lang.Object)
     */
    @Override
    public void addElement(InternalDate element) {
        if (mSource != null) {
            mSource.add(element);
            notifyDataSetChanged();         
        }
    }
    
    public void setChecks(List<Check> checks) {
    	mChecks = checks;
    }
    
    public void refreshDays(Calendar month) {
        releaseTmpObjs();
        month.set(Calendar.DAY_OF_MONTH, 1);
        final int mon                       = month.get(Calendar.MONTH);
        final int year                      = month.get(Calendar.YEAR);
        final int firstDay                  = month.get(Calendar.DAY_OF_WEEK);
        final int days                      = month.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i < firstDay; ++i) {
            addElement(InternalDate.DUMMY);
        }
        for (int i = 0; i < days; ++i) {
            final InternalDate internalDate = POOL.allocate();
            internalDate.mDay               = month.get(Calendar.DAY_OF_MONTH);
            internalDate.mMonth             = month.get(Calendar.MONTH);
            internalDate.mYear              = month.get(Calendar.YEAR);
            month.add(Calendar.DATE, 1);
            addElement(internalDate);
        }
        month.set(year, mon, 1);      
    }    
        
    public void releaseTmpObjs() {
        if (mSource != null) {
            for (int i = 0, s = mSource.size(); i < s; ++i) {
                final InternalDate internalDate = mSource.get(i);
                if (internalDate == InternalDate.DUMMY) {
                    // Skip it cause DUMMY object doesn't belong to our
                    // internal pool
                    continue;
                }
                POOL.release(internalDate);
            }
            clear();
        }
    }
    
    protected boolean contains(InternalDate date) {
    	if ((mChecks != null) && (!mChecks.isEmpty())) {
    		DUMMY_SEARCH.mDay   = date.mDay;
    		DUMMY_SEARCH.mMonth = date.mMonth;
    		DUMMY_SEARCH.mYear  = date.mYear;
    		return mChecks.contains(DUMMY_SEARCH);
    	}
    	return false;
    }
}