/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.util;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import com.yattatech.dbtc.Constants;
import com.yattatech.dbtc.DBTCApplication;
import com.yattatech.dbtc.R;
import com.yattatech.dbtc.log.Debug;

/**
 * Utility class that knows how to create a {@link Twitter} object
 * used to connect to twitter service
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class TwitterUtil {

	private static final String TAG = "TwitterUtil";
	private static Twitter sTwitter;
	private static RequestToken sRequestToken;
	
	private TwitterUtil() {
		throw new AssertionError();
	}
	
	/**
	 * Whenever the user request login operation invalid {@link RequestToken}
	 * 
	 */
	public static void invalidRequestToken() {
		Debug.d(TAG, "invalidRequestToken");
		sTwitter      = null;
		sRequestToken = null;
	}
	
	/**
	 * Workaround needed cause in some devices as Galaxy S2 and Morotola
	 * Milestone, that ref is cleaned after make twitter authentication 
	 * process
	 * 
	 */
	public static RequestToken getRequestToken() throws TwitterException {
		Debug.d(TAG, "getRequestToken");
		if (sRequestToken == null) {
			sRequestToken = getTwitter().getOAuthRequestToken(Constants.TWITTER_CALLBACK);
			if (Debug.isDebugable()) {
				Debug.d(TAG, "RequstToken=" + sRequestToken);
			}
		}
		return sRequestToken;
	}
	
	public static Twitter getTwitter() {
		if (sTwitter == null) {
			final ConfigurationBuilder builder = new ConfigurationBuilder();		
			builder.setHttpConnectionTimeout(Constants.TWITTER_TIMEOUT);
			sTwitter                           = new TwitterFactory(builder.build()).getInstance();
			// These values represents DBTC Twitter app credentials, don't
			// expose that data to anyone else
			final String twitterKey    = DBTCApplication.sApplicationContext.getString(R.string.twitter_key);
			final String twitterSecret = DBTCApplication.sApplicationContext.getString(R.string.twitter_secret);
			sTwitter.setOAuthConsumer(twitterKey, twitterSecret);									  
		}
		return sTwitter;
	}
}
