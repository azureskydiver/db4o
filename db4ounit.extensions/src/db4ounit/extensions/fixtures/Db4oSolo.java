/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;

import com.db4o.*;
import com.db4o.config.*;

public class Db4oSolo extends AbstractFileBasedDb4oFixture {

	public Db4oSolo() {
		this(new IndependentConfigurationSource());	
	}

	public Db4oSolo(ConfigurationSource configSource) {
		super(configSource,"db4oSoloTest.yap");	
	}
    
	protected ObjectContainer createDatabase(Configuration config) {
		return Db4o.openFile(config,getAbsolutePath());
	}

	public String getLabel() {
		return "SOLO";
	}
}
