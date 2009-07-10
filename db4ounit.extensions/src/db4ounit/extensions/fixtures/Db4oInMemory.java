/* Copyright (C) 2004 - 2006  Versant Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;

import db4ounit.extensions.*;

public class Db4oInMemory extends AbstractSoloDb4oFixture {
    
	public Db4oInMemory() {
		super();
	}
	
	public Db4oInMemory(FixtureConfiguration fc) {
		this();
		fixtureConfiguration(fc);
	}
	
	@Override
	public boolean accept(Class clazz) {
		if (!super.accept(clazz)) {
			return false;
		}
		if (OptOutInMemory.class.isAssignableFrom(clazz)) {
			return false;
		}
		return true;
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

	public String label() {
		return buildLabel("IN-MEMORY");
	}

	public void defragment() throws Exception {
		// do nothing
		// defragment is file-based for now
	}
	
}
