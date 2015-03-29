/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.domain;

import java.io.Serializable;

import com.yattatech.dbtc.log.Debug;

/**
 * Abstract class that represents a domain object of
 * DBTC system
 *
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 * 
 */
public abstract class Domain implements Serializable {
	
	private static final long serialVersionUID = 8940962013241294959L;
	protected static final boolean DEBUG       = Debug.isDebugable();
	
	protected Domain() {
		if (DEBUG) {
			final String classname = getClass().getSimpleName(); 
			Debug.d(classname, "Allocating memory to " + classname);
		}		
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {				
		if (DEBUG) {
			final String classname = getClass().getSimpleName(); 
			Debug.d(classname, "Deallocating memory from " + classname);			
		}		
	}
}
