/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.receiver;

import com.yattatech.dbtc.log.Debug;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import static com.yattatech.dbtc.DBTCApplication.sApplicationContext;

/**
 * Class where user can regiter/unregister {@link BroadcastReceiver} as well
 * send messages to them. It is totally up to the programmer to make housekeeping
 * avoiding any dangling references.
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class Broadcaster {

    private static final String TAG = "Broadcaster";
    
    private Broadcaster() {
        throw new AssertionError();
    }
    
    public static void registerLocalReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        if (Debug.isDebugable()) {
            Debug.d(TAG, "registering receiver=" + receiver +
                          " intentFilter="        + filter);
        } 
        LocalBroadcastManager.getInstance(sApplicationContext).registerReceiver(receiver, filter);
    }
    
    public static void unregisterLocalReceiver(BroadcastReceiver receiver) {
        if (Debug.isDebugable()) {
            Debug.d(TAG, "unregistering receiver=" + receiver);
        }
        try {
            LocalBroadcastManager.getInstance(sApplicationContext).unregisterReceiver(receiver);
        } catch (Throwable t) {
            // let's not bother user with any kind of exception
            Debug.e(TAG, "Failed:", t);
        }
    }
    
    public static void sendLocalMessage(Intent intent) {
        if (Debug.isDebugable()) {
            Debug.d(TAG, "sending message=" + intent);
        }
        LocalBroadcastManager.getInstance(sApplicationContext).sendBroadcast(intent);
    }
}
