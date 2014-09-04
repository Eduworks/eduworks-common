package com.eduworks.lang;

import java.util.Random;


public class EwRandom
{
	static Random	r	= new Random();

	public static boolean onein6()
	{
		return (Math.random() * 6.0) < 1;
	}

	public static String stringAny(int length)
	{
		byte[] bytes = new byte[length];
		r.nextBytes(bytes);
		return new String(bytes);
	}

}
