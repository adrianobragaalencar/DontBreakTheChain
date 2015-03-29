/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.util;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Abstract adapter class for watching editable events. 
 * All methods are empty and it just exists for convenience 
 * for creating listener objects 
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public abstract class TextWatcherAdapter implements TextWatcher {

	/*
	 * (non-Javadoc)
	 * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
	 */
	@Override
	public void afterTextChanged(Editable s) {
	}

	/*
	 * (non-Javadoc)
	 * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence, int, int, int)
	 */
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	/*
	 * (non-Javadoc)
	 * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence, int, int, int)
	 */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}
}
