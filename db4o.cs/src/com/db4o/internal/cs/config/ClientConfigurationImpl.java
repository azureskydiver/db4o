/* Copyright (C) 2008  db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.config;

import com.db4o.cs.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.messaging.*;

public class ClientConfigurationImpl extends NetworkingConfigurationProviderImpl implements ClientConfiguration {

	public ClientConfigurationImpl(Config4Impl config) {
		super(config);
	}

	public MessageSender messageSender() {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public void prefetchIDCount(int prefetchIDCount) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public void prefetchObjectCount(int prefetchObjectCount) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}
}
