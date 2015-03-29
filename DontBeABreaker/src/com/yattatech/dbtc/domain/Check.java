/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Domain used to represent a mark done in an specific
 * task timeline
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
@DatabaseTable(tableName = "check")
public final class Check extends Domain implements Parcelable {

	private static final long serialVersionUID = 1165026162331820519L;
	@Expose(serialize = false, deserialize = false)
	@DatabaseField(generatedId = true, columnName = "check_id")	
	public int mId;
	@SerializedName("task_id")
	@DatabaseField(columnName = "task_id")
	public int mTaskId;
	@Expose
	@SerializedName("day")
	@DatabaseField(columnName = "day")
	public int mDay;
	@Expose
	@SerializedName("month")
	@DatabaseField(columnName = "month")
	public int mMonth;
	@Expose
	@SerializedName("year")
	@DatabaseField(columnName = "year")
	public int mYear;	
	
	public Check() {		
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
        dest.writeInt(mId);
        dest.writeInt(mDay);
        dest.writeInt(mMonth);
        dest.writeInt(mYear);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
	    int result = 47;
	    result    *= mDay;
	    result    *= mMonth;
	    result    *= mYear;
	    return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
	    if (o == this) return true;
	    if (o == null) return false;
	    if (o.getClass() == getClass()) {
	        Check other = (Check)o;
	        return (mDay   == other.mDay)   &&
	               (mMonth == other.mMonth) &&
	               (mYear  == other.mYear);
	    }
	    return false;
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

	public static final Parcelable.Creator<Check> CREATOR = new Parcelable.Creator<Check>() {
	     
	    /*
	     * (non-Javadoc)
	     * @see android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
	     */
	    @Override
	    public Check createFromParcel(Parcel in) {
	        final Check check = new Check(); 
	        check.mId         = in.readInt();
	        check.mDay        = in.readInt();
	        check.mMonth      = in.readInt();
	        check.mYear       = in.readInt();
	        return check;
	    }

	    /*
	     * (non-Javadoc)
	     * @see android.os.Parcelable.Creator#newArray(int)
	     */
	    @Override
	    public Check[] newArray(int size) {
	        return new Check[size];
	    }
	};
}
