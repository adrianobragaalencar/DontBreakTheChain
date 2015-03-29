/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc;

import java.text.DateFormat;

/**
 * Constant class which keeps all values used by whole
 * application
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class Constants {
	
	public static final String DB_NAME                = "DBTC.db";
	public static final int DB_VERSION                = 1;	
	public static final int DEFAULT_THREADS_NUM       = 12;
	public static final int TWITTER_SUCCCESS          = 1;
	public static final int MIN_CALENDAR_DATE[]       = { 0, 2013 };
	public static final int TWITTER_TIMEOUT           = 3000;
	public static final int MAX_TASKLIST_SIZE         = 1500;
	public static final int MAX_DEFINED_TASK_TIME     = 365;
	public static final String TASK_KEY               = "@@@taskKey";
	public static final String TWITTER_LOGIN_KEY      = "@@@twitterLoginKey";
	public static final String RESTORE_KEY            = "@@@restoreKey";
	public static final String DATE_TITLE_FORMAT      = "MMMM yyyy";
	public static final String PREFS_NAME             = "dbtcPrefs";
	public static final String TWITTER_CALLBACK       = "dbtc://oauth";
	public static final String TWITTER_URL            = "@@@twitterUrl";
	public static final String TWITTER_REQUEST_TOKEN  = "@@@twitterRequestToken";
	public static final String TWITTER_CALLBACK_VALUE = "@@@twitterCallback";
	public static final String TWITTER_TOKEN          = "@@@twitterToken";
	public static final String TWITTER_TOKEN_SECRET   = "@@@twitterTokenSecret";
	public static final String OAUTH_VERIFIER         = "oauth_verifier";
	public static final String DROPBOX_TOKEN          = "@@@dropBoxToken";
	public static final String BACKUP_FILE_NAME       = "@@@DBTC_DontBeABreakBackup.json";
	public static final String TASKS_KEY              = "@@@tasksKey";
	public static final DateFormat DATE_FORMAT        = android.text.format.DateFormat.getDateFormat(DBTCApplication.sApplicationContext);	
	
	private Constants() {	
	    throw new AssertionError();
	}
}
