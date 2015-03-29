/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.adapter;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yattatech.dbtc.DBTCApplication;
import com.yattatech.dbtc.R;
import com.yattatech.dbtc.util.DateUtil;
import com.yattatech.dbtc.util.StringUtils;

/**
 * Adapter responsible for creating calendar header elements
 * which indicates every day of week
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class CalendarDayAdapter extends GenericAdapter<String> {

    public CalendarDayAdapter() {
        mSource = new ArrayList<String>();
        addDaysDesc();
    }
    
    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view                = convertView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            LayoutInflater vi    = (LayoutInflater)DBTCApplication.sApplicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view                 = vi.inflate(R.layout.calendar_item, null);            
        }
        final TextView dayView   = (TextView)view.findViewById(R.id.date);  
        final ImageView imgCheck = (ImageView)view.findViewById(R.id.dateChecked);        
        // Reset previous elements created and set by any 
        // previous calendar view
        dayView.setText(StringUtils.EMPTY_STR);
        imgCheck.setVisibility(View.INVISIBLE);
        final String day = getItem(position);  
        if (StringUtils.isEmpty(day)) {
            dayView.setClickable(false);
            dayView.setFocusable(false);
            return view;
        } 
        dayView.setText(day);
        return view;
    }
    
    public void updateDaysDesc() {
        addDaysDesc();
    }
    
    private void addDaysDesc() {
    	final String[] dayDesc = DateUtil.getDaysDesc();
        clear();
        addElement(dayDesc[Calendar.SUNDAY]);
        addElement(dayDesc[Calendar.MONDAY]);
        addElement(dayDesc[Calendar.TUESDAY]);
        addElement(dayDesc[Calendar.WEDNESDAY]);
        addElement(dayDesc[Calendar.THURSDAY]);
        addElement(dayDesc[Calendar.FRIDAY]);
        addElement(dayDesc[Calendar.SATURDAY]);
    }    
}
