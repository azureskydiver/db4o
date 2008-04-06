/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;

import com.db4o.*;
import com.db4o.config.*;

import db4ounit.extensions.*;
import db4ounit.extensions.util.*;

public class Db4oSolo extends AbstractFileBasedDb4oFixture {
	
	private static final String FILE = "db4oSoloTest.yap"; 

	public Db4oSolo() {
		this(new IndependentConfigurationSource());	
	}

	public Db4oSolo(ConfigurationSource configSource) {
		super(configSource, filePath());	
	}
    
	public Db4oSolo(FixtureConfiguration fixtureConfiguration) {
		this();
		fixtureConfiguration(fixtureConfiguration);
	}

	protected ObjectContainer createDatabase(Configuration config) {
		return Db4o.openFile(config, getAbsolutePath());
	}
	
	public String label() {
		return buildLabel("SOLO");
	}

	public void defragment() throws Exception {
		defragment(filePath());
	}

	private static String filePath() {
		return CrossPlatformServices.databasePath(FILE);
	}
}
