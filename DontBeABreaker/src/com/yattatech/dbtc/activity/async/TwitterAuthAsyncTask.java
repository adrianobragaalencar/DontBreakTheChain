/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.activity.async;

import twitter4j.auth.RequestToken;
import android.content.Intent;
import android.os.AsyncTask;
import com.yattatech.dbtc.Constants;
import com.yattatech.dbtc.R;
import com.yattatech.dbtc.activity.GenericFragmentActivity;
import com.yattatech.dbtc.activity.TwitterAuthenticatorScreen;
import com.yattatech.dbtc.activity.TwitterWriteScreen;
import com.yattatech.dbtc.log.Debug;
import com.yattatech.dbtc.util.TwitterUtil;

/**
 * AsyncTask responsible for loging into Twitter
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class TwitterAuthAsyncTask extends AsyncTask<Void, Void, Integer> {
	
	private static final String TAG            = "TwitterAuthAsyncTask";
	private static final Integer NO_CONNECTION = Integer.valueOf(0);
	private static final Integer FAILED        = Integer.valueOf(1);
	private static final Integer SUCCESS       = Integer.valueOf(2);
	private final GenericFragmentActivity mActivity;
	
	public TwitterAuthAsyncTask(GenericFragmentActivity activity) {
		mActivity = activity;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Integer doInBackground(Void... params) {
		Integer result = SUCCESS;
		try {
			if (!mActivity.isConnected()) {
				result = NO_CONNECTION;					
			} else if (GenericFragmentActivity.FACADE.isTwitterLogged()) {					
				mActivity.startNewActivity(TwitterWriteScreen.class);					
			} else {
				TwitterUtil.invalidRequestToken();
				final RequestToken requestToken = TwitterUtil.getRequestToken();
				final String authUrl            = requestToken.getAuthorizationURL();
				final Intent intent             = new Intent(mActivity, TwitterAuthenticatorScreen.class);
				intent.putExtra(Constants.TWITTER_URL,           authUrl);
				intent.putExtra(Constants.TWITTER_REQUEST_TOKEN, requestToken);
				mActivity.startActivityForResult(intent, Constants.TWITTER_SUCCCESS);					
			}
		} catch (Exception e) {
			Debug.e(TAG, "Failed:", e);
			result = FAILED;
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(Integer result) {
		if (result == NO_CONNECTION) {
			mActivity.showMessage(R.string.lab_no_connection);	
		} else if (result == FAILED) {
			mActivity.showMessage(R.string.lab_twitter_unknow);				
		} else {
			Debug.d(TAG, "TwitterAuthAsyncTask success");
		}			
	}
}
