package com.eduworks.util.io;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import com.eduworks.lang.threading.EwThreading;
import com.eduworks.lang.threading.EwThreading.MyRunnable;

public class EwDB
{
	public DB				db;
	public Object	handleLock	= new Object();
	public Future<?>   tCleose = null;
	public AtomicInteger		handles = new AtomicInteger(0);
	static Map<String,EwDB> cache = new HashMap<String,EwDB>();

	public static synchronized EwDB get(String _baseDirectory, String _databaseName)
	{
		String cacheKey = _baseDirectory + " " + _databaseName;

		EwDB lsh = null;
		lsh = (EwDB) cache.get(cacheKey);
		if (lsh != null)
		{
			synchronized(lsh.handles)
			{
				lsh.handles.incrementAndGet();
			}
			return lsh;
		}
		lsh = new EwDB();
		new File(_baseDirectory).mkdirs();
		new File(_baseDirectory).mkdir();
		lsh.db = DBMaker.newFileDB(new File(_baseDirectory, _databaseName)).cacheSoftRefEnable().closeOnJvmShutdown().make();
		cache.put(cacheKey, lsh);
		lsh.handles.incrementAndGet();
		return lsh;
	}
	
	public void commit() {
		db.commit();
	}

	public static synchronized EwDB getCompressed(String _baseDirectory, String _databaseName)
	{
		String cacheKey = _baseDirectory + " " + _databaseName;

		EwDB lsh = null;
		lsh = (EwDB) cache.get(cacheKey);
		if (lsh != null)
		{
			synchronized(lsh.handles)
			{
				lsh.handles.incrementAndGet();
			}
			return lsh;
		}
		lsh = new EwDB();
		new File(_baseDirectory).mkdirs();
		new File(_baseDirectory).mkdir();
		lsh.db = DBMaker.newFileDB(new File(_baseDirectory, _databaseName)).cacheSoftRefEnable().compressionEnable().closeOnJvmShutdown().make();
		cache.put(cacheKey, lsh);
		lsh.handles.incrementAndGet();
		return lsh;
	}
	
    protected final ReentrantReadWriteLock commitLock = new ReentrantReadWriteLock();
	public boolean	compact = false;
	public AtomicInteger writeCount = new AtomicInteger(0);
    
	public synchronized void close()
	{
		if (handles.decrementAndGet() == 0)
			if (tCleose == null || tCleose.isDone())
				tCleose = EwThreading.fork(new MyRunnable(){
	
					@Override
					public void run()
					{
						while (handles.get() != 0)
							EwThreading.sleep(5000);
						synchronized(handles)
						{
							while (handles.get() != 0)
								EwThreading.sleep(5000);
							if (compact || writeCount.get() > 0)
							{
//								System.out.println("Committing.");
								db.commit();
//								System.out.println("Committed.");
							}
							if (compact)
							{
								compact = false;
//								System.out.println("Compacting.");
//								db.compact();
//								System.out.println("Compacted.");
							}
						}
					}
				});
	}

	public void compact()
	{
		db.compact();
	}

}
