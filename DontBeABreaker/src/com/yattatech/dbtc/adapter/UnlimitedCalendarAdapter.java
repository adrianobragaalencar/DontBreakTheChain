package com.yattatech.dbtc.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.yattatech.dbtc.activity.AbstractChainTaskScreen;
import com.yattatech.dbtc.domain.InternalDate;
import com.yattatech.dbtc.util.CurrentDateUtil;

/**
 * {@link CalendarAdapter} that represents an unlimited one
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class UnlimitedCalendarAdapter extends CalendarAdapter {

	public UnlimitedCalendarAdapter(AbstractChainTaskScreen context) {
		super(context);
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
        if (date.isGreaterThanCurrentDay(CurrentDateUtil.sCurrentDate)) {
            mDayView.setClickable(false);
            mDayView.setFocusable(false);            
            mDayView.setTextColor(mGrayColor);
        } else {
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
