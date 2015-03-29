/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import android.content.Context;

import com.yattatech.dbtc.DBTCApplication;
import com.yattatech.dbtc.log.Debug;

/**
 * Utility class which can copy current database to sdcard
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class DbUtil {

	private static final String TAG = DbUtil.class.getSimpleName();
	
	private DbUtil() {
		throw new AssertionError();
	}
	
    /**
     * Copies the application database to given folder, it' very useful
     * for debugging purpose only.
     * 
     * @param path
     * 
     */
    public static void copyDatabase(String path) {
        final File folder     = new File(path);
        final Context context = DBTCApplication.sApplicationContext;
        if (!folder.exists()) {
        	folder.exists();
        }               
        for (String database : context.databaseList()) {
            final File dbFile = context.getDatabasePath(database);
            InputStream in    = null;
            OutputStream out  = null;
            try {
            	in  = new FileInputStream(dbFile);
                out = new FileOutputStream(new File(folder, database));
                IOUtils.copy(in, out);
            } catch (IOException ioe) {
            	Debug.d(TAG, "Failed:", ioe);
            } finally {
            	IOUtils.closeQuietly(in);
            	IOUtils.closeQuietly(out);
            }
        }
    }
}
