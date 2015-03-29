/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.domain;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Domain used to represent a bunch of {@link Task}
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class TaskList extends Domain {

	private static final long serialVersionUID = 9061606232627767514L;
	
	@Expose
	@SerializedName("checksum")
	public long mChecksum;
	@Expose
	@SerializedName(value = "tasks")
	public List<Task> mTasks;
	
	public TaskList() {	
	}
}
