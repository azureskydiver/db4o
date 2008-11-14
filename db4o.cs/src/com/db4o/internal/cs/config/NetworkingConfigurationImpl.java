/* Copyright (C) 2008  db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.config;

import com.db4o.cs.config.NetworkingConfiguration;
import com.db4o.internal.Config4Impl;
import com.db4o.messaging.MessageRecipient;

public class NetworkingConfigurationImpl implements NetworkingConfiguration {

	protected final Config4Impl _config;

	NetworkingConfigurationImpl(Config4Impl config) {
		_config = config;
	}

	public Config4Impl config() {
		return _config;
	}

	public void batchMessages(boolean flag) {
		_config.batchMessages(flag);
		
	}

	public void maxBatchQueueSize(int maxSize) {
		_config.maxBatchQueueSize(maxSize);
	}

	public void singleThreadedClient(boolean flag) {
		_config.singleThreadedClient(flag);
	}

	public void timeoutClientSocket(int milliseconds) {
		_config.timeoutClientSocket(milliseconds);
	}

	public void timeoutServerSocket(int milliseconds) {
		_config.timeoutServerSocket(milliseconds);
	}
	
	public void messageRecipient(MessageRecipient messageRecipient) {
		_config.setMessageRecipient(messageRecipient);
	}

}