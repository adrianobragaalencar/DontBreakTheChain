/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yattatech.dbtc.R;
import com.yattatech.dbtc.activity.MainScreen;
import com.yattatech.dbtc.domain.Task;
import com.yattatech.dbtc.util.DateUtil;

/**
 * Adapter to show {@link Task} objects
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class TaskListAdapter extends GenericAdapter<Task> {
	
	private final StringBuilder mBuffer = new StringBuilder(256);
    private final MainScreen mContext;
	private TextView mTxTaskName;
	private TextView mTxDate;
	private TextView mTxTaskChecks;
	
	public TaskListAdapter(MainScreen context) {
	    mContext = context;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
		final View view               = inflater.inflate(R.layout.task_item, parent, false);
		final Task task               = getItem(position);
		mTxTaskName                   = (TextView)view.findViewById(R.id.txTaskname);
		mTxDate                       = (TextView)view.findViewById(R.id.txTaskDate);
		mTxTaskChecks                 = (TextView)view.findViewById(R.id.txTaskChecks);
		if (task != null) {
			mTxTaskName.setText(task.mName);			
			mTxDate.setText(DateUtil.format(task.mYearCreation, 
											task.mMonthCreation + 1,  // plus 1 cause month in java begins with 0 
											task.mDayCreation));
			if (task.mFinished) {
				mTxTaskChecks.setText(mContext.getString(R.string.lab_finished));	
			} else if (task.mDefined) {
				mBuffer.append(mContext.getChecksCountByTask(task))
				       .append(" - ")
				       .append(task.mMaxDays);
				mTxTaskChecks.setText(mBuffer);
				mBuffer.setLength(0);
			} else {
				final int resId       = mContext.isTaskChecked(task) ? R.string.lab_checked : R.string.lab_not_checked;
				mTxTaskChecks.setText(mContext.getString(resId));				
			}
		}		
		return view;		
	}		
}
