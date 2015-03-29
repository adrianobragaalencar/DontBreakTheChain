/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.fragments;

import android.support.v4.app.Fragment;

/**
 * Skeleton implementation to be used as base by all
 * fragments
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public abstract class GenericFragment extends Fragment {
	
	protected String mTag;
	
	protected GenericFragment() {
		mTag = getClass().getSimpleName();
	}	
}
