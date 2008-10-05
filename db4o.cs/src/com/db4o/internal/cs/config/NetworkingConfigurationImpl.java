/* Copyright (C) 2008  db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.config;

import com.db4o.cs.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

public class NetworkingConfigurationImpl implements NetworkingConfiguration {

	protected final Config4Impl _config;

	NetworkingConfigurationImpl(Config4Impl config) {
		_config = config;
	}

	public Config4Impl config() {
		return _config;
	}

	public void batchMessages(boolean flag) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public void maxBatchQueueSize(int maxSize) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public void singleThreadedClient(boolean flag) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public void timeoutClientSocket(int milliseconds) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	public void timeoutServerSocket(int milliseconds) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

}