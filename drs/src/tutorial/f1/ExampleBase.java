/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package f1;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;

/**
 * Base class with common functionality for replication examples:
 */
public abstract class ExampleBase {
	
	protected ObjectContainer openObjectContainer(String fileName) {
		
		// note the configuration that has to be set
		return Db4oEmbedded.openFile(embeddedDb4oConfiguration(), fileName);
		
	}
	
	protected EmbeddedConfiguration embeddedDb4oConfiguration(){
		
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		
		// The following two configuration settings are required
		// for db4o replication to work:
		
		config.file().generateUUIDs(ConfigScope.GLOBALLY);
		config.file().generateCommitTimestamps(true);
		
		
		return config;
		
	}
	
	protected String db4oFileName(){
		return "example.db4o";
	}
	
	protected void deleteDb4oDatabaseFile() {
		new File(db4oFileName()).delete();
	}


}
