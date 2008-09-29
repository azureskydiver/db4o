/* Copyright (C) 2008  db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.config;

import com.db4o.cs.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.messaging.*;

public class ServerConfigurationImpl extends NetworkingConfigurationProviderImpl implements ServerConfiguration {

	public ServerConfigurationImpl(Config4Impl config) {
		super(config);
	}

	public void messageRecipient(MessageRecipient messageRecipient) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public LocalConfiguration local() {
		return new LocalConfigurationImpl(config());
	}

	public BaseConfiguration base() {
		return new BaseConfigurationImpl(config());
	}
}
