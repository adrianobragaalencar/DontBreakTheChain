/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.dao;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import com.yattatech.dbtc.Constants;
import com.yattatech.dbtc.DBTCApplication;
import com.yattatech.dbtc.domain.Task;
import com.yattatech.dbtc.domain.TwitterLoginStatus;
import com.yattatech.dbtc.log.Debug;
import com.yattatech.dbtc.util.TwitterUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import static com.yattatech.dbtc.Constants.TWITTER_TOKEN;
import static com.yattatech.dbtc.Constants.TWITTER_TOKEN_SECRET;
import static com.yattatech.dbtc.Constants.DROPBOX_TOKEN;

/**
 * System class that holds DBTC preferences 
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class SystemPreferences {
	
	private static final String TAG = "SystemPreferences";
	private final SharedPreferences mPrefs;
	
	public SystemPreferences() {
		mPrefs = DBTCApplication.sApplicationContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
	}
	
	public void twitterLogout() {
	    Debug.d(TAG, "twitterLogout");
	    mPrefs.edit()
              .putString(TWITTER_TOKEN,        null)
              .putString(TWITTER_TOKEN_SECRET, null)
              .commit();
	}

    public void dropboxLogout() {
        Debug.d(TAG, "dropboxLogout");
        mPrefs.edit()
              .putString(DROPBOX_TOKEN, null)
              .commit();
    }	
	
	public boolean isTwitterLogged() {
		// Don't log nothing about twitter tokens for security reason
		// someone could use adb to take log messages and find out
		// easily current token
		Debug.d(TAG, "isTwitterLogged");
		final String token       = mPrefs.getString(TWITTER_TOKEN,        null); 
		final String tokenSecret = mPrefs.getString(TWITTER_TOKEN_SECRET, null); 
		return (token != null) && (tokenSecret != null);
	}
	
	public TwitterLoginStatus setTwitterData(final Bundle bundle) {
		if (Debug.isDebugable()) {
			Debug.d(TAG, "setTwitterData b=" + bundle);	
		}
		TwitterLoginStatus status = TwitterLoginStatus.UNKNOWN;
		try {
			final RequestToken requestToken = (RequestToken)bundle.get(Constants.TWITTER_REQUEST_TOKEN);
			final String twitterCallback    = bundle.getString(Constants.TWITTER_CALLBACK_VALUE);		
			if (Debug.isDebugable()) {
			    Debug.d(TAG, "twitterCallback=" + twitterCallback);    
			}
			if (twitterCallback.contains("denied")) {
			    status = TwitterLoginStatus.DENIED;
			} else {
	            final Uri url                   = Uri.parse(twitterCallback);
	            final Twitter twitter           = TwitterUtil.getTwitter();       
	            final String verifier           = url.getQueryParameter(Constants.OAUTH_VERIFIER);                                      
	            final AccessToken accessToken   = twitter.getOAuthAccessToken(requestToken, verifier);
	            if (accessToken == null) {
	                Debug.d(TAG, "Unable to create AccessToken Twitter");
	                status = TwitterLoginStatus.FAILED;
	            } else {
                    mPrefs.edit()
                          .putString(TWITTER_TOKEN,        accessToken.getToken())
                          .putString(TWITTER_TOKEN_SECRET, accessToken.getTokenSecret())
                          .commit();         
                    status = TwitterLoginStatus.SUCCESS;
	            }          			    
			}
		} catch (Exception e) {
			Debug.e(TAG, "Failed:", e);			
		}
		return status;
	}
	
	public AccessToken getTwitterAccessToken() {
		Debug.d(TAG, "getTwitterAccessToken");
		// Don't log nothing about twitter tokens for security reason
		// someone could use adb to take log messages and find out
		// easily current token		
		final String token       = mPrefs.getString(TWITTER_TOKEN,        null); 
		final String tokenSecret = mPrefs.getString(TWITTER_TOKEN_SECRET, null); 
		return new AccessToken(token, tokenSecret);
	}
	
	public void setDropBoxToken(final String token) {
	    Debug.d(TAG, "setDropBoxToken");
        mPrefs.edit()
              .putString(DROPBOX_TOKEN, token)
              .commit();         
	}
	
	public String getDropBoxToken() {
	    Debug.d(TAG, "getDropBoxToken");
	    return mPrefs.getString(DROPBOX_TOKEN, null);
	}
	
	public void saveDataOrder(List<Task> tasks) {
        if (Debug.isDebugable()) {
            Debug.d(TAG, "saveDataOrder tasks=" + tasks);
        }
	    if ((tasks == null) || (tasks.isEmpty())) {
	        return;
	    }
        final int size             = tasks.size();
        final StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < size; ++i) {
            buffer.append(tasks.get(i).mId)
                  .append(',');
        }
        buffer.deleteCharAt(buffer.length() - 1);
        mPrefs.edit()
              .putString(Constants.TASKS_KEY, buffer.toString())
              .commit();
	}
	
	public List<Task> restoreDataOrder(List<Task> tasks) {
        if (Debug.isDebugable()) {
            Debug.d(TAG, "restoreDataOrder tasks=" + tasks);
        }
        final String value  = mPrefs.getString(Constants.TASKS_KEY, null);
        if (value == null) {
            return tasks;
        }
        final String[] ids  = value.split(",");
        final int length    = ids.length;
        if ((length <= 0)            ||
            (length != tasks.size()) || 
            (length > Constants.MAX_TASKLIST_SIZE)) {
            return tasks;
        }
        final List<Task> newList = new ArrayList<Task>();
        for (int i = 0; i < length; ++i) {
            final Task task = getByTaskId(tasks, Integer.valueOf(ids[i]));
            if (task != null) {
                newList.add(task);
            }    
        }
        if (!newList.isEmpty()) {
            tasks = newList;
        }
        return tasks;
	}
	
   private Task getByTaskId(List<Task> tasks, int taskId) {
        final int size = tasks.size();
        for (int i = 0; i < size; ++i) {
            if (tasks.get(i).mId == taskId) {
                return tasks.remove(i);
            }
        }
        return null;
    }
}
