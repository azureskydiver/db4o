/* Copyright (C) 2008  Versant Inc.   http://www.db4o.com */

package com.db4o.cs.internal.config;

import com.db4o.config.*;
import com.db4o.cs.config.*;
import com.db4o.internal.*;
import com.db4o.internal.config.*;
import com.db4o.messaging.*;

public class ClientConfigurationImpl extends NetworkingConfigurationProviderImpl implements ClientConfiguration {

	public ClientConfigurationImpl(Config4Impl config) {
		super(config);
	}

	public MessageSender messageSender() {
		return legacy().getMessageSender();
	}

	public void prefetchIDCount(int prefetchIDCount) {
		legacy().prefetchIDCount(prefetchIDCount);
	}

	public void prefetchObjectCount(int prefetchObjectCount) {
		legacy().prefetchObjectCount(prefetchObjectCount);
	}

	public CommonConfiguration common() {
		return Db4oLegacyConfigurationBridge.asCommonConfiguration(legacy());
	}

	public void prefetchDepth(int prefetchDepth) {
		legacy().prefetchDepth(prefetchDepth);
    }

	public void prefetchSlotCacheSize(int slotCacheSize) {
		legacy().prefetchSlotCacheSize(slotCacheSize); 
	}
	
	public void timeoutClientSocket(int milliseconds) {
		legacy().timeoutClientSocket(milliseconds);
	}

	public int timeoutClientSocketValue() {
		return legacy().timeoutClientSocket();
	}

}
