/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.activity;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.yattatech.dbtc.domain.Task;
import com.yattatech.dbtc.facade.SystemFacade;
import com.yattatech.dbtc.log.Debug;
import com.yattatech.dbtc.util.HardwareUtil;

/**
 * Base activity to be used by whole application
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public abstract class GenericFragmentActivity extends FragmentActivity {
    	
    public static final SystemFacade FACADE = SystemFacade.UNIQUE;
    protected final String mTag;
    protected ProgressDialog mProgressDialog;
    protected static List<Task> sTasks;
    
    protected GenericFragmentActivity() {
    	mTag = getClass().getSimpleName();
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Debug.isDebugable()) {
        	// To avoid unneeded string creating we really 
        	// must do a double check on log 
        	Debug.d(mTag, "onCreate(...) " + mTag);	
        }        
        setRequestedOrientation(HardwareUtil.IS_TABLET ?
        						ActivityInfo.SCREEN_ORIENTATION_PORTRAIT :
        		                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {        
        if (Debug.isDebugable()) {
        	Debug.d(mTag, "onCreateOptionsMenu(...) " + mTag);	
        }                    	
        return super.onCreateOptionsMenu(menu);
    }    

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if (Debug.isDebugable()) {
        	Debug.d(mTag, "onCreateContextMenu(...) " + mTag);	
        }                    	    	
    	super.onCreateContextMenu(menu, v, menuInfo);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (Debug.isDebugable()) {
        	Debug.d(mTag, "onStart() " + mTag);	
        }                
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (Debug.isDebugable()) {
        	Debug.d(mTag, "onResume() " + mTag);	
        }                
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (Debug.isDebugable()) {
        	Debug.d(mTag, "onPause() " + mTag);	
        }                
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (Debug.isDebugable()) {
        	Debug.d(mTag, "onStop() " + mTag);	
        }                
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Debug.isDebugable()) {
        	Debug.d(mTag, "onDestroy() " + mTag);	
        }                
    }
            
    /*
     * (non-Javadoc)
     * @see android.app.Activity#startActivity(android.content.Intent)
     */
    @Override
    public void startActivity(Intent intent) {
        if (Debug.isDebugable()) {
        	Debug.d(mTag, "startActivity() " + 
                          " intent="         + intent +
                          " tag="            + mTag);	
        }                    	
        super.startActivity(intent);
    }
    
	public boolean isConnected() {
		final ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo networkInfo                 = connectivityManager.getActiveNetworkInfo();
		return (networkInfo != null) && (networkInfo.isConnectedOrConnecting());
	}
    
	public void showMessage(int msgId) {
    	final String msg = getString(msgId);
    	Debug.d(mTag, msg);
    	Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
    
	public void showMessage(int msgId, Object ...args) {
    	String msg = getString(msgId);
    	msg        = String.format(msg, args);
    	Debug.d(mTag, msg);
    	Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
    
    protected void cancelAsyncTask(AsyncTask<?, ?, ?> asyncTask) {
        Debug.d(mTag, "cancelAsyncTask");
        if ((asyncTask != null) && (asyncTask.getStatus() != Status.FINISHED)) {
            try {
                asyncTask.cancel(true);
            } catch (Exception e) {
                Debug.e(mTag, "Failed:", e);
            }
        }
    }   
    
    public void showSpinnerProgressBar(int titleId) {       
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setTitle(titleId);
        mProgressDialog.show();
    }
    
    public void showProgressBar(int current, int max) {       
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setProgress(current);
        mProgressDialog.setMax(max);        
        mProgressDialog.show();
    }
    
    public void startNewActivity(Class<?> clazz) {
    	if (Debug.isDebugable()) {
    		Debug.d(mTag, "startActivity packageContext=" + this +
    				      "Class="                        + clazz); 
    	}
    	final Intent intent = new Intent(this, clazz);
    	startActivity(intent);
    }
}
