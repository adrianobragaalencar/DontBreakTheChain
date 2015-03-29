/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.io;

import java.io.FilterInputStream;
import java.io.InputStream;

/**
 * Special {@link InputStream} used only to know in advanced 
 * the total bytes occupied by it. That's supposed to be total
 * in memory
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class BackupInputStream extends FilterInputStream {

    public long mLength;
    
    public BackupInputStream(InputStream in) {
        super(in);
    }
}
