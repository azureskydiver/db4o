/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.omplus.datalayer;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;

public class OMEDataStore {
	private final static String SEPARATOR ="/";
	
	private final String dbPath;
	private final ContextPrefixProvider prefixProvider;
	private  OMEData omeData;
	
	public OMEDataStore(String dbPath, ContextPrefixProvider prefixProvider){
		this.dbPath = dbPath;
		this.prefixProvider = prefixProvider;
		ObjectContainer db = getObjectContainer();
		omeData = readOMEData(db);
		if(omeData == null) {
			omeData = new OMEData();
			db.store(omeData);
		}
		close(db);
	}
		
	@SuppressWarnings("unchecked")
	public <T> ArrayList<T> getGlobalEntry(String key){
		if(key == null) {
			return null;
		}
		return (ArrayList<T>)omeData.data.get(key);
	}
	
	public <T> void setGlobalEntry(String key, ArrayList<T> list){
		if(key == null || list == null) {
			return;
		}
		omeData.data.put(key, list);
		writeData(omeData);
	}
	
	public <T> ArrayList<T> getContextLocalEntry(String key){
		return getGlobalEntry(getContextPrefixedKey(key));
	}
	
	public <T> void setContextLocalEntry(String key, ArrayList<T> list){
		setGlobalEntry(getContextPrefixedKey(key), list);
	}

	public boolean getIsLastConnRemote() {
		return omeData.isLastConnRemote;
	}

	public void setIsLastConnRemote(boolean isLastConnRemote) {
		omeData.isLastConnRemote = isLastConnRemote;
	}

	private String getContextPrefixedKey(String key) {
		return prefixProvider.currentPrefix() + SEPARATOR + key;
	}
	
	private synchronized void writeData(OMEData omData) {
		ObjectContainer db = getObjectContainer();
		OMEData temp = readOMEData(db);
		if (temp != null){
			temp.data = omData.getData();
			temp.isLastConnRemote = omData.isLastConnRemote;
		}
		else {
			temp = omeData;
		}
		db.store(temp);
		close(db);
	}

	private ObjectContainer getObjectContainer() {
		return Db4oEmbedded.openFile(configure(), dbPath);
	}

	private OMEData readOMEData(ObjectContainer db){
		ObjectSet<OMEData> result = db.query(OMEData.class);
		return result.hasNext() ? result.next() : null;
	}
	
	private EmbeddedConfiguration configure() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().objectClass(OMEData.class).minimumActivationDepth(Integer.MAX_VALUE);
		config.common().objectClass(OMEData.class).updateDepth(Integer.MAX_VALUE);
		config.common().allowVersionUpdates(true);
		return config;
	}
	
	private void close(ObjectContainer db){
		db.commit();
		db.close();
	}

	public void close() {
	}
}
