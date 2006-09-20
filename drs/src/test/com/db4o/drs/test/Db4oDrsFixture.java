/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.io.MemoryIoAdapter;
import com.db4o.replication.db4o.Db4oReplicationProvider;


public class Db4oDrsFixture implements DrsFixture {

	String _name;
	ExtObjectContainer _db;
	TestableReplicationProviderInside _provider;
	
	public Db4oDrsFixture(String name) {
		_name = name;
	}
	
	// FIXME: escape _name
	private String yapFileName() {
		return "drs" + _name + ".yap";
	}
	
	public TestableReplicationProviderInside provider() {
		return _provider;
	}

	public void clean() {
		new File(yapFileName()).delete();
	}

	public void close() {
		_provider.destroy();
		_db.close();
	}

	public ExtObjectContainer db() {
		return _db;
	}

	public void open() {
		MemoryIoAdapter memoryIoAdapter = new MemoryIoAdapter();
		Db4o.configure().io(memoryIoAdapter);
		
		_db = Db4o.openFile(new File(yapFileName()).getPath()).ext();
		_provider = new Db4oReplicationProvider(_db, _name);
	}
}
