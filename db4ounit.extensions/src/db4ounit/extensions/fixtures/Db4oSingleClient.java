/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;


public class Db4oSingleClient extends AbstractClientServerDb4oFixture {
	
	public Db4oSingleClient(ConfigurationSource config, boolean embeddedClient) {
		super(config, FILE, embeddedClient); 
	}

	public Db4oSingleClient(boolean embeddedClient) {
		this(new IndependentConfigurationSource(), embeddedClient);
	}
	
	public String getLabel() {
		return "C/S SINGLE-CLIENT";
	}

}
