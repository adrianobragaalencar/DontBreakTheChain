/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.adapter;

import java.util.Collections;
import java.util.List;

import android.widget.BaseAdapter;
/**
 * Skeleton implementation to all Adapter objects
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public abstract class GenericAdapter<T> extends BaseAdapter {

	protected List<T> mSource;
		
	public void setElements(List<T> elements) {
		mSource = elements;
		notifyDataSetChanged();
	}
		
	public void addElement(T element) {
		if ((mSource != null) && (!mSource.contains(element))) {
			mSource.add(element);
			notifyDataSetChanged();			
		}
	}
		
	public void removeElement(T element) {
        if ((mSource != null) && (mSource.contains(element))) {
            mSource.remove(element);
            notifyDataSetChanged();         
        }	    
	}	

	public List<T> getSource() {
		return (mSource == null) ? Collections.<T>emptyList() : mSource;
	}
	
	public void clear() {
		if (mSource != null) {
			mSource.clear();	
		}		
	}
	
	public boolean hasElements() {
	    return getCount() > 0;
	}
	
	public void insertElementAt(T object, int index) {
	    if (mSource != null) {
	        mSource.add(index, object);
	        notifyDataSetChanged();
	    }
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return (mSource == null) ? 0 : mSource.size();
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public T getItem(int position) {
		if ((mSource != null) && (position < mSource.size())) {
			return mSource.get(position);	
		}
		return null;		
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}	
}
