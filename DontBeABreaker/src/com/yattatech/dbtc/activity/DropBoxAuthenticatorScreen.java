/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.activity;

import android.os.Bundle;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.yattatech.dbtc.R;
import com.yattatech.dbtc.log.Debug;
import com.yattatech.dbtc.util.StringUtils;

/**
 * Screen where user can log in DropBox service
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class DropBoxAuthenticatorScreen extends GenericFragmentActivity {

    private DropboxAPI<AndroidAuthSession> mDropboxAPI;
    private String mToken;
    
    /*
     * (non-Javadoc)
     * @see com.yattatech.dbtc.activity.GenericFragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String appKey              = getString(R.string.dropbox_key);
        final String appSecret           = getString(R.string.dropbox_secret);
        final AppKeyPair pair            = new AppKeyPair(appKey, appSecret);
        final AndroidAuthSession session = new AndroidAuthSession(pair);
        mDropboxAPI                      = new DropboxAPI<AndroidAuthSession>(session);
        mToken                           = FACADE.getDropBoxToken();
    }

    /*
     * (non-Javadoc)
     * @see com.yattatech.dbtc.activity.GenericFragmentActivity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        final AndroidAuthSession session = mDropboxAPI.getSession();
        if (StringUtils.isEmpty(mToken)) {
            Debug.d(mTag, "Empty DropBox AccessToken");
            session.startOAuth2Authentication(this);
        } else {
            Debug.d(mTag, "Valid DropBox AccessToken");
            session.setOAuth2AccessToken(mToken);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.yattatech.dbtc.activity.GenericFragmentActivity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mDropboxAPI.getSession().isLinked()) {
            Debug.d(mTag, "DropBox already linked");
        } else if (mDropboxAPI.getSession().authenticationSuccessful()) {
            mDropboxAPI.getSession().finishAuthentication();
            mToken = mDropboxAPI.getSession().getOAuth2AccessToken();
            FACADE.setDropBoxToken(mToken);
        } else {
            mToken = FACADE.getDropBoxToken();
            mDropboxAPI.getSession().setOAuth2AccessToken(mToken);
        }
    }    
}
