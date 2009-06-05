/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.omplus.datalayer;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;

public class OMEDataStore {
	private final static String SEPARATOR ="/";
	
	private final ContextPrefixProvider prefixProvider;
	private  OMEData omeData;
	private ObjectContainer db;
	
	public OMEDataStore(String dbPath, ContextPrefixProvider prefixProvider){
		this.prefixProvider = prefixProvider;
		db = Db4oEmbedded.openFile(configure(), dbPath);
		omeData = readOMEData(db);
		if(omeData == null) {
			omeData = new OMEData();
		}
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
		writeData();
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
	
	private void writeData() {
		db.store(omeData);
		db.commit();
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
	
	public void close() {
		db.close();
	}
}
