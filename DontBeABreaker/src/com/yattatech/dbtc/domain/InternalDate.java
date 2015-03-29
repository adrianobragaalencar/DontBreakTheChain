/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Domain to be used by our custom CalendarUI component
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class InternalDate extends Domain implements Parcelable {
    
    private static final long serialVersionUID = 9105587875521996911L;
    public static final InternalDate DUMMY     = new InternalDate();
    public int mDay;
    public int mMonth;
    public int mYear;
    
    public InternalDate() {        
    }
    
    public String getDay() {
        return String.valueOf(mDay);
    }
    
    public boolean isLessThanCurrentDay(InternalDate other) {
        return other.isGreaterThanCurrentDay(this);
    }
    
    public boolean isEqualsTo(InternalDate other) {
        return (mYear  == other.mYear)  &&
               (mMonth == other.mMonth) &&
               (mDay   == other.mDay); 
    }    
    
    public boolean isGreaterThanCurrentDay(InternalDate other) {  
        return (mYear  >= other.mYear)  &&
               (mMonth >= other.mMonth) &&
               (mDay   >  other.mDay);
    }
    
    public void reset() {
        mDay   =
        mMonth =
        mYear  = 0;
    }
    
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
        dest.writeInt(mDay);
        dest.writeInt(mMonth);
        dest.writeInt(mYear);
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (DEBUG) {
            return new StringBuilder().append("day=")
                                      .append(mDay)
                                      .append(" month=")
                                      .append(mMonth)
                                      .append(" year=")
                                      .append(mYear)
                                      .toString();            
        }
        return super.toString();
    }
    
    public static final Parcelable.Creator<InternalDate> CREATOR = new Parcelable.Creator<InternalDate>() {
        
        /*
         * (non-Javadoc)
         * @see android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
         */
        @Override
        public InternalDate createFromParcel(Parcel in) {
            final InternalDate date = new InternalDate(); 
            date.mDay               = in.readInt();
            date.mMonth             = in.readInt();
            date.mYear              = in.readInt();
            return date;
        }

        /*
         * (non-Javadoc)
         * @see android.os.Parcelable.Creator#newArray(int)
         */
        @Override
        public InternalDate[] newArray(int size) {
            return new InternalDate[size];
        }
    };
}
