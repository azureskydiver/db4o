package com.db4o.omplus.connection;

import com.db4o.Db4o;
import com.db4o.config.Configuration;
import com.db4o.config.DotnetSupport;

public abstract class DbConnection {
	
	private int DEPTH = 10;
	
	public abstract String getPath();
	public abstract boolean isRemote();
	
//	TODO: should get the activation and update depth from preferences
	public Configuration configure(){
		Configuration config = Db4o.newConfiguration();
		config.activationDepth(DEPTH);
		config.updateDepth(DEPTH);
		config.add(new DotnetSupport());
		return config;
	}

}
