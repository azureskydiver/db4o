/* Copyright (C) 2008  db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.config;

import com.db4o.config.*;
import com.db4o.cs.config.ServerConfiguration;
import com.db4o.internal.Config4Impl;
import com.db4o.internal.config.*;

public class ServerConfigurationImpl extends NetworkingConfigurationProviderImpl implements ServerConfiguration {

	public ServerConfigurationImpl(Config4Impl config) {
		super(config);
	}
	
	public CacheConfiguration cache() {
		return new CacheConfigurationImpl(legacy());
	}

	public FileConfiguration file() {
		return new FileConfigurationImpl(legacy());
	}

	public CommonConfiguration common() {
		return new CommonConfigurationImpl(legacy());
	}
	
	
}
