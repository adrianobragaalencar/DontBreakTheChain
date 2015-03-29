/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.activity;

import com.yattatech.dbtc.Constants;
import com.yattatech.dbtc.R;
import com.yattatech.dbtc.log.Debug;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import twitter4j.auth.RequestToken;

/**
 * Screen where user can log in Twitter
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class TwitterAuthenticatorScreen extends GenericFragmentActivity {

	private String mUrl;
	private RequestToken mToken;
	
	/*
	 * (non-Javadoc)
	 * @see com.yattatech.dbtc.activity.GenericFragmentActivity#onCreate(android.os.Bundle)
	 */	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		final WebView webView = (WebView)findViewById(R.id.webView);
		final Bundle bundle   = getIntent().getExtras();		
		mUrl                  = bundle.getString(Constants.TWITTER_URL);
		mToken                = (RequestToken)bundle.get(Constants.TWITTER_REQUEST_TOKEN);		
		
		WebSettings webSettings = webView.getSettings();
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webSettings.setPluginState(PluginState.ON);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setJavaScriptEnabled(true);		
		webSettings.setBuiltInZoomControls(false);
		webSettings.setDatabaseEnabled(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setAppCacheEnabled(false);		
		webView.setWebViewClient(new WebViewClient() {
			
			/*
			 * (non-Javadoc)
			 * @see android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit.WebView, java.lang.String)
			 */
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (Debug.isDebugable()) {
					Debug.d(mTag, "url=" + url);
				}
				if (url.startsWith(Constants.TWITTER_CALLBACK)) {
					 final Intent intent = new Intent();
					 intent.putExtra(Constants.TWITTER_CALLBACK_VALUE, url);
					 intent.putExtra(Constants.TWITTER_REQUEST_TOKEN,  mToken);
					 setResult(Constants.TWITTER_SUCCCESS, intent);
					 finish();
					 return true;
				 }
				 return super.shouldOverrideUrlLoading(view, url);
			}			
		});
		webView.loadUrl(mUrl);
	}
}

