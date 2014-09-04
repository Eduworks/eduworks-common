package com.eduworks.util.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;


public class InMemoryFile
{
	public String name;
	public String mime;
	public byte[] data;
	public InMemoryFile()
	{
		// TODO Auto-generated constructor stub
	}
	public InMemoryFile(File source) throws IOException
	{
		name = source.getName();
		data = FileUtils.readFileToByteArray(source);
	}
	public InputStream getInputStream()
	{
		return new ByteArrayInputStream(data);
	}
}
