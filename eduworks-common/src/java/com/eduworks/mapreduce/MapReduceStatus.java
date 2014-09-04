package com.eduworks.mapreduce;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MapReduceStatus
{
	public enum STATE{OK,FAILED,IN_QUESTION};
	
	public MapReduceTransport transport = null;
	private String	host;
	private String	name;
	private STATE	state;
	private int	I;
	private short	port;
	
	@Override
	public String toString()
	{
		return host + ":" + port + " #" + I + " " + state;
	}
	
	public void setState(STATE state)
	{
		this.state = state;
	}
	public void setI(int i)
	{
		this.I = i;
	}
	public STATE getState()
	{
		return state;
	}
	public boolean notOK()
	{
		return state != STATE.OK;
	}
	public void setHost(String host,short port)
	{
		this.host = host;
		this.port = port;
	}
	public void setServiceName(String name)
	{
		this.name = name;
	}
	public MapReduceTransport getInterface() throws RemoteException, NotBoundException
	{
		if (transport != null && state == STATE.OK)
			return transport;
        Registry registry = LocateRegistry.getRegistry(host,port);
        transport = (MapReduceTransport) registry.lookup(name);
        return transport;
	}

}
