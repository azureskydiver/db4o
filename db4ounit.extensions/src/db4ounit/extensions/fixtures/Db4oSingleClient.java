/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;

import java.io.IOException;

import com.db4o.Db4o;
import com.db4o.ext.ExtObjectContainer;

import db4ounit.TestException;

public class Db4oSingleClient extends AbstractClientServerDb4oFixture {

	private ExtObjectContainer _objectContainer;

	public Db4oSingleClient(ConfigurationSource config, String fileName,
			int port) {
		super(config, fileName, port);
	}
	
	public Db4oSingleClient(ConfigurationSource config, int port) {
		super(config, FILE, port);
	}

	public Db4oSingleClient(ConfigurationSource config) {
		super(config);
	}

	public Db4oSingleClient() {
		this(new IndependentConfigurationSource());
	}

	public void close() throws Exception {
		_objectContainer.close();
		super.close();
	}

	public void open() throws Exception {
		super.open();
		try {
			_objectContainer = _port == 0
				? openEmbeddedClient().ext()
				: Db4o.openClient(config(), HOST, _port, USERNAME, PASSWORD).ext();
		} catch (IOException e) {
			e.printStackTrace();
			throw new TestException(e);
		}
	}
	

	public ExtObjectContainer db() {
		return _objectContainer;
	}

	public String getLabel() {
		return "C/S SINGLE-CLIENT";
	}

}
