/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.dao;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.yattatech.dbtc.Constants;
import com.yattatech.dbtc.domain.Check;
import com.yattatech.dbtc.domain.Task;
import com.yattatech.dbtc.log.Debug;

/**
 * Utility class responsible for creating and upgrading
 * DBTC database.
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String TAG = "DatabaseHelper";
	
	public DatabaseHelper(Context context) {
		super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
	}

	/*
	 * (non-Javadoc)
	 * @see com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase, com.j256.ormlite.support.ConnectionSource)
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource conn) {
		Debug.d(TAG, "onCreate");
		try {
			TableUtils.createTable(conn, Task.class);
			TableUtils.createTable(conn, Check.class);
		} catch (SQLException e) {
			Debug.d(TAG, "Failure:", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, com.j256.ormlite.support.ConnectionSource, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource conn, int oldVersion, int newVersion) {
		Debug.d(TAG, "onUpgrade");
		try {
			TableUtils.dropTable(conn, Task.class,  true);
			TableUtils.dropTable(conn, Check.class, true);
		} catch (SQLException e) {
			Debug.d(TAG, "Failure:", e);
		}		
	}
	
	public final <T, ID> Dao<T, ID> getDaoLayer(Class<T> clazz) {
		Dao<T, ID> dao = null;
		try {
			dao = getDao(clazz);
			if (Debug.isDebugable()) {
				Debug.d(TAG, "getDaoLayer=" + clazz.getSimpleName() +
						     " dao="        + dao);	
			}			
		} catch (SQLException e) {
			Debug.e(TAG, "Failed:", e);
		}			
		return dao;
	}
} 
