/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;

import java.io.*;

import com.db4o.*;
import com.db4o.ext.*;

import db4ounit.*;


public class Db4oMultiClient extends AbstractClientServerDb4oFixture{
    
    public Db4oMultiClient(ConfigurationSource configSource) {
		super(configSource);
	}

    public Db4oMultiClient() {
		this(new IndependentConfigurationSource());
	}

	public ExtObjectContainer db() {
        try {
            return Db4o.openClient(config(),HOST, PORT, USERNAME, PASSWORD).ext();
        } catch (IOException e) {
            e.printStackTrace();
            throw new TestException(e);
        }
    }

	public String getLabel() {
		return "C/S MULTI-CLIENT";
	}
}
