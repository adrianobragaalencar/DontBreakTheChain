/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.activity;

/**
 * Class that has all messages to be used to update
 * some kind of ui in application
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class AsyncMessages {

    public static final int ADD_NEW_TASK_SUCCESS     = 0x001;
    public static final int ADD_NEW_TASK_FAILED      = 0x002;
    public static final int EDIT_TASK_SUCCESS        = 0x004;
    public static final int EDIT_TASK_FAILED         = 0x008;    
    public static final int REMOVE_TASK_SUCCESS      = 0x010;
    public static final int REMOVE_TASK_FAILED       = 0x020;
    public static final int BACKUP_RESTORE_SUCCESS   = 0x040;
    public static final int BACKUP_RESTORE_FAILED    = 0x080;
    public static final String ADD_NEW_TASK_ACTION   = "com.yattatech.dbtc.action.ADD_NEW_TASK";
    public static final String EDIT_TASK_ACTION      = "com.yattatech.dbtc.action.EDIT_TASK";
    public static final String REMOVE_TASK_ACTION    = "com.yattatech.dbtc.action.REMOVE_TASK";
    public static final String TWITTER_LOGIN_ACTION  = "com.yattatech.dbtc.action.TWITTER_LOGIN";
    public static final String BACKUP_RESTORE_ACTION = "com.yattatech.dbtc.action.BACKUP_RESTORE";
    
    private AsyncMessages() {
        throw new AssertionError();
    }
}
