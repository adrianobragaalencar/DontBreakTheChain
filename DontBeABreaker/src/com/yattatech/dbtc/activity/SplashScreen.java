/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.activity;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.crittercism.app.Crittercism;
import com.yattatech.dbtc.R;
import com.yattatech.dbtc.adapter.AnimationAdapter;
import com.yattatech.dbtc.log.Debug;
import com.yattatech.dbtc.util.DbUtil;

/**
 * Main Entry Screen for our Don't Break the chain 
 * application.
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class SplashScreen extends GenericFragmentActivity {
    
	private Animation mAnim;
	private View mView;
	private volatile boolean mRequestMainActivity = true;
	
	private final AnimationAdapter mViewAnimationListener = new AnimationAdapter() {
		
		/*
		 * (non-Javadoc)
		 * @see com.yattatech.dbtc.adapter.AnimationAdapter#onAnimationEnd(android.view.animation.Animation)
		 */
		@Override
		public void onAnimationEnd(Animation animation) {
			// To avoid any weird effect after animation has been done, let's remove
			// them as soon as finished
			mView.setAnimation(null);
            if (mRequestMainActivity) {
            	startNewActivity(MainScreen.class);
            } 
            finish();
		}
	};
	
	
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		if (FACADE.isDeviceClockOutOfSync()) {
			Debug.d(mTag, "System clock is not supported");
			showMessage(R.string.system_clock_outof_sync);
			finish();
			return;
		}
        setContentView(R.layout.layout_splash_screen);
        mView = findViewById(R.id.content);
        mAnim = AnimationUtils.loadAnimation(this, R.anim.activity_open_scale);
        mView.setAnimation(mAnim);
        mAnim.setAnimationListener(mViewAnimationListener);
		Crittercism.initialize(getApplicationContext(), getString(R.string.crittercism_id));
		// make sure set android:debuggable as false, avoiding 
		// execution of bellow code block in release version
		final ApplicationInfo info = getApplicationInfo();		
		if ((info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
			Debug.d(mTag, "defining thread policy");
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
																  .detectDiskReads()
																  .detectDiskWrites()
																  .detectNetwork()
																  .penaltyLog()
																  .build());
			Debug.d(mTag, "defining VM policy");
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
														  .detectLeakedSqlLiteObjects()
														  .penaltyLog()
														  .build());	
		}
		if (Debug.isDebugable()) {
			// Usefull to know current database state using some
			// external sqlite tool to check it out, for copying
			// that file to computer use adb command line
			DbUtil.copyDatabase(Environment.getExternalStorageDirectory().getAbsolutePath());
		}
    }
    
    /*
     * (non-Javadoc)
     * @see com.yattatech.dbtc.activity.GenericFragmentActivity#onStart()
     */
    @Override
    protected void onStart() {
    	super.onStart();
    	mView.startAnimation(mAnim);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        // Disable onbackpressed behavior but be aware
        // that it's going to die after animation  has
        // been finished
    	mRequestMainActivity = false;
    }
}
