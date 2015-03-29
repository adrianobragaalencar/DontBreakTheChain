/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.activity;

import android.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

import com.yattatech.dbtc.R;
import com.yattatech.dbtc.util.StringUtils;

/**
 * Screen where user can write a twitter message 
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class TwitterWriteScreen extends GenericFragmentActivity {
	
	private TextView mBtnSend;
	private TextView mBtnLogout;
	private EditText mTxTwitter;
	private final OnKeyListener mTxTwitterKeyListener = new OnKeyListener() {

		/*
		 * (non-Javadoc)
		 * @see android.view.View.OnKeyListener#onKey(android.view.View, int, android.view.KeyEvent)
		 */
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if ((keyCode == KeyEvent.KEYCODE_ENTER) &&
			    (event.getAction() == KeyEvent.ACTION_DOWN)) {
				if (mTxTwitter.getLineCount() >= 10) {
					return true;
				}
			}
			return false;
		}
	};
	private final OnClickListener mBtnListener = new OnClickListener() {

		/*
		 * (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
		    if (v == mBtnSend) {
		        sendTwitterMsg();
		    } else {  //(v == mBtnLogout) 
		        twitterLogout();
		    }
		}
	};
	
	/*
	 * (non-Javadoc)
	 * @see com.yattatech.dbtc.activity.GenericFragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_twitter_write);
		mBtnSend                  = (TextView)findViewById(R.id.btnSend);
		mBtnLogout                = (TextView)findViewById(R.id.btnLogout);
		mTxTwitter                = (EditText)findViewById(R.id.txtTweet);
	    final int color           = getResources().getColor(R.color.grey2);
	    final ActionBar actionBar = getActionBar();
	    mBtnSend.setOnClickListener(mBtnListener);
	    mBtnLogout.setOnClickListener(mBtnListener);
	    mTxTwitter.setOnKeyListener(mTxTwitterKeyListener);	    
        actionBar.setIcon(R.drawable.ic_twitter);
        actionBar.setTitle(R.string.app_name);
        actionBar.setBackgroundDrawable(new ColorDrawable(color));		
	}
	
	private void sendTwitterMsg() {
        final String msg = mTxTwitter.getText().toString();
        if (isConnected()) {
            if (StringUtils.isEmpty(msg)) {
                showMessage(R.string.lab_twitter_empty);
            } else {
                FACADE.sendTwitterMsg(msg);
                mTxTwitter.setText(StringUtils.EMPTY_STR);
            }               
        } else {
            showMessage(R.string.lab_no_connection);
        }
	}
	
	private void twitterLogout() {
	    FACADE.twitterLogout();
	    finish();
	}
}
