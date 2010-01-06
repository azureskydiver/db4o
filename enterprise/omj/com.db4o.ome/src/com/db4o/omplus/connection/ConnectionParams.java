package com.db4o.omplus.connection;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;

public abstract class ConnectionParams {
	
	protected boolean allowUpdates = false;
	
	public abstract String getPath();
	// TODO get rid of me
	public abstract boolean isRemote();
	public abstract ObjectContainer connect(Function4<String, Boolean> userCallback) throws DBConnectException;
	
	public final ObjectContainer connect() throws DBConnectException {
		return connect(new Function4<String, Boolean>() {
			public Boolean apply(String arg0) {
				return false;
			}
		});
	}
	
//	TODO: should get the activation and update depth from preferences
	protected void configureCommon(CommonConfiguration config){
		config.allowVersionUpdates(allowUpdates);
		config.activationDepth(0);
		config.add(new DotnetSupport(true));
	}
	
	public void configureUpdates() {
		allowUpdates = true;
	}
	
	@Override
	public boolean equals(Object other) {
		if(this == other) {
			return true;
		}
		if(other == null || getClass() != other.getClass()) {
			return false;
		}
		return getPath().equals(((ConnectionParams)other).getPath());
	}
	
	@Override
	public int hashCode() {
		return getPath().hashCode();
	}
	
	public String toString() {
		return getPath();
	}

}
