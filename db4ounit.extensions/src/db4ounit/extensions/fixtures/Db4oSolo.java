/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.config.Configuration;
import com.db4o.foundation.io.*;

public class Db4oSolo extends AbstractFileBasedDb4oFixture {
	
	private static final String FILE = "db4oSoloTest.yap"; 

	public Db4oSolo() {
		this(new IndependentConfigurationSource());	
	}

	public Db4oSolo(ConfigurationSource configSource) {
		super(configSource,filePath());	
	}

	private static String filePath() {
		String path = System.getProperty("db4ounit.file.path");
		if(path == null || path.length() == 0) {
			path =".";
		}
		return Path4.combine(path, FILE);
	}
    
	protected ObjectContainer createDatabase(Configuration config) {
		return Db4o.openFile(config,getAbsolutePath());
	}
	
	public String getLabel() {
		return "SOLO";
	}

	public void defragment() throws Exception {
		defragment(filePath());
	}
}
