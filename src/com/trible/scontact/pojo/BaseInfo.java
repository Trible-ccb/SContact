package com.trible.scontact.pojo;

import java.lang.reflect.Type;

import com.j256.ormlite.misc.BaseDaoEnabled;

public abstract class BaseInfo extends BaseDaoEnabled<BaseInfo, Long>{

	public static final Long INVALID_ID = -1L;
	public abstract Type listType();
}
