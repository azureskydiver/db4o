/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.monitoring.cs;

import static com.db4o.foundation.Environments.my;

import java.io.IOException;

import com.db4o.ObjectContainer;
import com.db4o.foundation.network.*;
import com.db4o.monitoring.Db4oMBeans;

@decaf.Ignore
public class MonitoredClientSocket4 extends MonitoredSocket4Base {

	protected MonitoredClientSocket4(Socket4 socket) {
		super(socket);		
	}
	
	@Override
	public void close() throws IOException {
		super.close();
		_bean.unregister();
	}
	
	@Override
	protected Networking bean() {
		if (null == _bean)
		{
			_bean = Db4oMBeans.newClientNetworkingStatsMBean(my(ObjectContainer.class)); 
		}
		
		return _bean; 
	}	
	
	private Networking _bean;
}
