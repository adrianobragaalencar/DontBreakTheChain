/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.activity;

import android.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.yattatech.dbtc.Constants;
import com.yattatech.dbtc.R;
import com.yattatech.dbtc.domain.Task;
import com.yattatech.dbtc.log.Debug;
import com.yattatech.dbtc.util.StringUtils;
import com.yattatech.dbtc.util.TextWatcherAdapter;

/**
 * Screen where the user can add new tasks to be chained
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class NewTaskScreen extends GenericFragmentActivity {
	
	private TextView mTxTitle;
	private EditText mEdtName;
	private TextView mBtnDone;
	private EditText mEdtMaxDays;
	private CheckBox mCBTime;
	private View mView;
	private int mMinValue;
	private int mMaxValue;
	private int mMaxChars;
	private Task mEditTask;	
	private final OnClickListener mBtnDoneListener = new OnClickListener() {

		/*
		 * (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {			
			final String taskName = mEdtName.getText().toString();
			if (StringUtils.isEmpty(taskName)) {
				showMessage(R.string.empty_task_name);
			} else if (isEmptyFixedTime()) {
				showMessage(R.string.empty_task_time);								
			} else if (isInvalidRange()) {
				final String values = mMinValue + "-" + mMaxValue;
				showMessage(R.string.task_time_invalid, values);
			} else {
				final String text     = mEdtMaxDays.getText().toString();
				final boolean checked = mCBTime.isChecked();
				final int maxDays     = StringUtils.isEmpty(text) ? 0 : Integer.parseInt(text);
				if (mEditTask == null) {
				    Debug.d(mTag, "add new task");
					FACADE.addNewTask(taskName, checked, maxDays);
				} else {
				    Debug.d(mTag, "edit task");				    
					mEditTask.mName    = taskName;
					mEditTask.mDefined = checked;
					mEditTask.mMaxDays = maxDays;
					FACADE.editTask(mEditTask);						
				}				
				finish();
			}
		}
	};
	private final OnCheckedChangeListener mCheckedChangeTimeListener = new OnCheckedChangeListener() {
		
		/*
		 * (non-Javadoc)
		 * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged(android.widget.CompoundButton, boolean)
		 */
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			mView.setVisibility(isChecked ? View.VISIBLE : View.GONE);
		}
	};
	private final class NumberTextWatcher extends TextWatcherAdapter {		
		
		private final String mMinValueStr;
		private final String mMaxValueStr;
		
		public NumberTextWatcher() {
			mMinValueStr = String.valueOf(mMinValue);
			mMaxValueStr = String.valueOf(mMaxValue);
		}
		
		/*
		 * (non-Javadoc)
		 * @see com.yattatech.dbtc.util.TextWatcherAdapter#afterTextChanged(android.text.Editable)
		 */
		@Override
		public void afterTextChanged(Editable s) {
			final int length   = s.length();
			final String value = s.toString();
			if (length >= mMaxChars) {
				final int num  = Integer.parseInt(value);
				if (num > mMaxValue) {
					s.delete(0, length);
					s.append(mMaxValueStr);
				} else if (num < mMinValue) {
					s.delete(0, length);
					s.append(mMinValueStr);
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.yattatech.dbtc.activity.GenericFragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.layout_newtask_screen);
	    final int color           = getResources().getColor(R.color.grey2);
	    final ActionBar actionBar = getActionBar();
		mTxTitle                  = (TextView)findViewById(R.id.txTitle);
		mEdtName                  = (EditText)findViewById(R.id.edtName);
		mBtnDone                  = (TextView)findViewById(R.id.btnAddTask);
		mEdtMaxDays               = (EditText)findViewById(R.id.maxDayPicker);
		mCBTime                   = (CheckBox)findViewById(R.id.checkBoxTime);
		mView                     = findViewById(R.id.maxDays);
		mMinValue                 = 1;
		mMaxValue                 = Constants.MAX_DEFINED_TASK_TIME;
		mMaxChars                 = String.valueOf(mMaxValue).length();
		final Bundle bundle       = getIntent().getExtras();
		final Task task           = (Task)((bundle == null) ? null : bundle.getParcelable(Constants.TASK_KEY));
		mBtnDone.setOnClickListener(mBtnDoneListener);
        actionBar.setIcon(R.drawable.ic_task);
        actionBar.setTitle(R.string.app_name);
        actionBar.setBackgroundDrawable(new ColorDrawable(color));
		// running on edit mode
		if (task != null) {
			mEditTask = task;
			mTxTitle.setText(R.string.lab_edit_task_title);
			mEdtName.setText(task.mName);
			// on edit mode user cannot change defined task to undefined
			// and vice-versa, the user can change number of days or the
			// current name that it.			
			mCBTime.setChecked(mEditTask.mDefined);			
			mView.setVisibility(mEditTask.mDefined ? View.VISIBLE : View.GONE);			
			mEdtMaxDays.setText(String.valueOf(mEditTask.mMaxDays));
			mCBTime.setEnabled(false);
		}
		mEdtMaxDays.addTextChangedListener(new NumberTextWatcher());
        mCBTime.setOnCheckedChangeListener(mCheckedChangeTimeListener);
	}	
	
	private boolean isEmptyFixedTime() {
		if (mCBTime.isChecked()) {
			final String maxDays = mEdtMaxDays.getText().toString();
			return StringUtils.isEmpty(maxDays);
		}
		return false;
	}
	
	private boolean isInvalidRange() {
		if (mCBTime.isChecked()) {
			final int maxDays = Integer.parseInt(mEdtMaxDays.getText().toString());
			return (maxDays < mMinValue) || (maxDays > mMaxValue); 
		}
		return false;
	}
}
