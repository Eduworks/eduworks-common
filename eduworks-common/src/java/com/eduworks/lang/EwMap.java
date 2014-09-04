package com.eduworks.lang;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class EwMap<E,T> extends LinkedHashMap<E,T> implements Serializable
{
	public EwMap(int initialCapacity)
	{
		super(initialCapacity);
	}

	public EwMap()
	{
	}
	
	public EwMap(Map<E, T> parameters)
	{
		if (parameters != null)
		for (Map.Entry<E,T> e: parameters.entrySet())
			put(e.getKey(),e.getValue());
	}

	public static final long serialVersionUID = 1L;
}
