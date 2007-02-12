/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;

public class Db4oInMemory extends AbstractSoloDb4oFixture {
    
	public Db4oInMemory() {
		super(new IndependentConfigurationSource());
	}

	public Db4oInMemory(ConfigurationSource configSource) {
		super(configSource);
	}

	private MemoryFile _memoryFile;
	
	protected ObjectContainer createDatabase(Configuration config) {
		if (null == _memoryFile) {
			_memoryFile = new MemoryFile();
		}
		return ExtDb4o.openMemoryFile(config,_memoryFile);
	}

    protected void doClean() {
    	_memoryFile = null;
    }

	public String getLabel() {
		return "IN-MEMORY";
	}

	public void defragment() throws Exception {
		// do nothing
		// defragment is file-based for now
	}
	
}
