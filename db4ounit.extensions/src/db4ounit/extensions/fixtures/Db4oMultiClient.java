/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;

import java.io.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.internal.*;

import db4ounit.*;

public class Db4oMultiClient extends AbstractClientServerDb4oFixture {

	private boolean _embeddedClient;

	public Db4oMultiClient(ConfigurationSource configSource, boolean embeddedClient) {
		super(configSource, embeddedClient);
	}

	public Db4oMultiClient(int clientsCount, boolean embeddedClient) {
		this(new IndependentConfigurationSource(), embeddedClient);
	}

	public String getLabel() {
		return "C/S MULTI-CLIENT";
	}
	
	public ExtObjectContainer openNewClient() {
		try {
			return _embeddedClient ? openEmbeddedClient().ext() : Db4o.openClient(cloneDb4oConfiguration((Config4Impl) config()), HOST, _port, USERNAME,PASSWORD).ext();
		} catch (IOException e) {
			throw new TestException(e);
		}
	}
	
	private Config4Impl cloneDb4oConfiguration(Config4Impl config) {
		return (Config4Impl) config.deepClone(this);
	}
}
