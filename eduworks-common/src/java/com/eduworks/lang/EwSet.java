package com.eduworks.lang;


public class EwSet<T> extends EwHashSet<T>
{

	public EwSet(EwList<T> asStrings)
	{
		addAll(asStrings);
	}

	public EwSet()
	{
	}

	public void addAll(T[] array)
	{
		for (T t : array)
			add(t);
	}

}
