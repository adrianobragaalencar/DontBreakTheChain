/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.adt;

import com.yattatech.dbtc.log.Debug;

/**
 * A very simple pool of T objects, acting as stack of TObjects 
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 * @param <T>
 * 
 */
public final class TPool<T> {
    
	private static final String TAG       = "TPool"; 
	private static final int DEFAULT_SIZE = 32;
    private T[] mObjects;
    private int mCurrent;
    private int mPoolSize;
    private String mType;
        
	public TPool(TPoolAllocator<T> allocator) {
	    this(DEFAULT_SIZE, allocator);
    }
    
	@SuppressWarnings("unchecked")
    public TPool(int size, TPoolAllocator<T> allocator) {
	    mPoolSize = size;
        mObjects  = (T[])new Object[size];
        for (int i = 0; i < size; ++i) {
            mObjects[i] = allocator.allocate();
        }
        mCurrent = size - 1;
        // Little hack just to get on the fly classname
        mType    = mObjects[0].getClass().getSimpleName();        
    }
    
    public T allocate() {
    	if (mCurrent < 0) {
    		throw new IllegalStateException("Pool exausted");
    	}
    	Debug.d(TAG, "allocating from Pool " + mType);
    	T object           = mObjects[mCurrent];
    	mObjects[mCurrent] = null;
    	--mCurrent;
        return object;
    }

    public void release(T t) {
    	if (mCurrent + 1 < mPoolSize) {
    		Debug.d(TAG, "releasing to Pool " + mType);
    		mObjects[++mCurrent] = t;
    	}        
    }
    
    /**
     * Class responsible for allocating a T object to 
     * Pool
     *
     */
    public static abstract class TPoolAllocator<T> {
    	public abstract T allocate();
    }
}
