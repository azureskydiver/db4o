package com.db4o.omplus.connection;

import com.db4o.Db4o;
import com.db4o.config.Configuration;

public abstract class ConnectionParams {
	
	protected boolean allowUpdates = false;
	
	public abstract String getPath();
	public abstract boolean isRemote();
	public abstract String toString();
	
//	TODO: should get the activation and update depth from preferences
	public Configuration configure(){
		Configuration config = Db4o.newConfiguration();
		config.allowVersionUpdates(allowUpdates);
		config.activationDepth(0);
		return config;
	}
	public void configureUpdates() {
		allowUpdates = true;
	}

}
