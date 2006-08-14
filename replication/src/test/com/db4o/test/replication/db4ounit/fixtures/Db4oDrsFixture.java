/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.test.replication.db4ounit.fixtures;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.inside.replication.ReplicationProviderInside;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.test.replication.db4ounit.DrsFixture;


public class Db4oDrsFixture implements DrsFixture {

	String _name;
	ExtObjectContainer _db;
	ReplicationProviderInside _provider;
	
	public Db4oDrsFixture(String name) {
		_name = name;
	}
	
	// FIXME: escape _name
	private String yapFileName() {
		return "drs" + _name + ".yap";
	}
	
	public ReplicationProviderInside provider() {
		return _provider;
	}

	public void clean() {
		new File(yapFileName()).delete();
	}

	public void close() throws Exception {
		_provider.destroy();
		_db.close();
	}

	public ExtObjectContainer db() {
		return _db;
	}

	public void open() throws Exception {
		_db = Db4o.openFile(new File(yapFileName()).getPath()).ext();
		_provider = new Db4oReplicationProvider(_db, _name);
	}
}
