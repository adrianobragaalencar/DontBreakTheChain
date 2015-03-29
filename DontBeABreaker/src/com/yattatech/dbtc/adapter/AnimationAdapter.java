/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.adapter;

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 * Abstract adapter class for receiving animation state 
 * events. All methods are empty and it just exists for 
 * convenience for creating listener objects 
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public abstract class AnimationAdapter implements AnimationListener {

	/*
	 * (non-Javadoc)
	 * @see android.view.animation.Animation.AnimationListener#onAnimationEnd(android.view.animation.Animation)
	 */
	@Override
	public void onAnimationEnd(Animation animation) {
	}

	/*
	 * (non-Javadoc)
	 * @see android.view.animation.Animation.AnimationListener#onAnimationRepeat(android.view.animation.Animation)
	 */
	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	/*
	 * (non-Javadoc)
	 * @see android.view.animation.Animation.AnimationListener#onAnimationStart(android.view.animation.Animation)
	 */
	@Override
	public void onAnimationStart(Animation animation) {
	}
}
