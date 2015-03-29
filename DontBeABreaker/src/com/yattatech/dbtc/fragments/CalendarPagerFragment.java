/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yattatech.dbtc.R;
import com.yattatech.dbtc.adapter.CalendarPagerAdapter;
import com.yattatech.dbtc.adapter.PageChangeListenerAdapter;

/**
 * Fragment that draws the current Calendar UI component
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class CalendarPagerFragment extends GenericFragment {
	
	private ViewPager mViewPager;
	private CalendarPagerAdapter mPagerAdapter;
	private final PageChangeListenerAdapter mChangeListenerAdapter = new PageChangeListenerAdapter() {
		
		/*
		 * (non-Javadoc)
		 * @see com.yattatech.dbtc.adapter.PageChangeListenerAdapter#onPageSelected(int)
		 */
		@Override
		public void onPageSelected(int position) {
		}
	}; 
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_calendar_pager, container, false);
		mViewPager      = (ViewPager)view.findViewById(R.id.calendarPage);
		mPagerAdapter   = new CalendarPagerAdapter(getFragmentManager());
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setOnPageChangeListener(mChangeListenerAdapter);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
}
