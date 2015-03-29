/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.domain;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Domain used to represent an user task
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
@DatabaseTable(tableName = "task")
public final class Task extends Domain implements Parcelable {

	private static final long serialVersionUID = 3984825709577425052L;
	@Expose(serialize = false, deserialize = false)
	@DatabaseField(generatedId = true, columnName = "id")	
	public int mId;
	@Expose
	@SerializedName("name")
	@DatabaseField(columnName = "name")
	public String mName;
	@Expose
	@SerializedName("day_creation")
	@DatabaseField(columnName = "day_creation")
	public int mDayCreation;
	@Expose
	@SerializedName("month_creation")
	@DatabaseField(columnName = "month_creation")
	public int mMonthCreation;
	@Expose
	@SerializedName("year_creation")
	@DatabaseField(columnName = "year_creation")
	public int mYearCreation;
	@Expose
	@SerializedName(value = "checks")
	public List<Check> mChecks;
	@Expose
	@SerializedName(value = "defined")
	@DatabaseField(columnName = "defined")
	public boolean mDefined;
	@Expose
	@SerializedName(value = "max_days")
	@DatabaseField(columnName = "max_days")
	public int mMaxDays;
	@Expose
	@SerializedName(value = "finished")	
	@DatabaseField(columnName = "finished")
	public boolean mFinished;
	
	public Task() {	
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
        dest.writeString(mName);
        dest.writeInt(mDayCreation);
        dest.writeInt(mMonthCreation);
        dest.writeInt(mYearCreation);
        dest.writeInt(mDefined  ? 1 : 0);                
        dest.writeInt(mFinished ? 1 : 0);
        dest.writeInt(mMaxDays);
    }
	
	/*
	 * 	(non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = 47;		
		result     = result * ((mName   == null) ? 1    : mName.hashCode());
		result     = result * (mMaxDays == 0     ? 1    : mMaxDays);
		result     = result * (mDefined          ? 1231 : 1237);
		result     = result * (mFinished         ? 1231 : 1237);				
		result     = result * mId;
		result     = result * mDayCreation;
		result     = result * mMonthCreation;
		result     = result * mYearCreation;
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
		if (getClass() == o.getClass()) {
			Task task = (Task)o;
			return (mName          == null ? task.mName == null : mName.equals(task.mName)) &&
				   (mId            == task.mId)                                             &&
				   (mDayCreation   == task.mDayCreation)                                    &&
				   (mMonthCreation == task.mMonthCreation)                                  &&
				   (mYearCreation  == task.mYearCreation)                                   &&
				   (mDefined       == task.mDefined)                                        &&
				   (mFinished      == task.mFinished)                                       &&
				   (mMaxDays       == task.mMaxDays);
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
			return new StringBuilder().append("id=")
									  .append(mId)
									  .append(" name=")
									  .append(mName)
									  .append(" dayCreation=")
									  .append(mDayCreation)
									  .append(" monthCreation=")
									  .append(mMonthCreation)
									  .append(" yearCreation=")
									  .append(mYearCreation)									  
                                      .append(" defined=")
                                      .append(mDefined)
                                      .append(" finished=")
                                      .append(mFinished)                                                                            
                                      .append(" maxDays=")
                                      .append(mMaxDays)                                      
									  .toString();
		}
		return mName;
	}
	
	 public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
	     
	     /*
	      * (non-Javadoc)
	      * @see android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
	      */
	     @Override
	     public Task createFromParcel(Parcel in) {
	         final Task task     = new Task(); 
	         task.mId            = in.readInt();
	         task.mName          = in.readString();
	         task.mDayCreation   = in.readInt();
	         task.mMonthCreation = in.readInt();
	         task.mYearCreation  = in.readInt();
	         task.mDefined       = in.readInt() == 1;
	         task.mFinished      = in.readInt() == 1;
	         task.mMaxDays       = in.readInt();
	         return task;
	     }

	     /*
	      * (non-Javadoc)
	      * @see android.os.Parcelable.Creator#newArray(int)
	      */
	     @Override
	     public Task[] newArray(int size) {
	    	 return new Task[size];
	     }
	};
}
