/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Enumeration that contains all possible values to
 * login operation status
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public enum TwitterLoginStatus implements Parcelable {
    
    SUCCESS,
    FAILED,
    DENIED,
    UNKNOWN;

    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ordinal());
    }
    
    public static final Creator<TwitterLoginStatus> CREATOR = new Creator<TwitterLoginStatus>() {
        
        /*
         * (non-Javadoc)
         * @see android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
         */
        @Override
        public TwitterLoginStatus createFromParcel(final Parcel source) {
            return TwitterLoginStatus.values()[source.readInt()];
        }

        /*
         * (non-Javadoc)
         * @see android.os.Parcelable.Creator#newArray(int)
         */
        @Override
        public TwitterLoginStatus[] newArray(final int size) {
            return new TwitterLoginStatus[size];
        }
    };
}
