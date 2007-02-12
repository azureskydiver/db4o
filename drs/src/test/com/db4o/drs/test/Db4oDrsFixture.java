/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.drs.db4o.Db4oProviderFactory;
import com.db4o.drs.inside.TestableReplicationProviderInside;
import com.db4o.ext.ExtObjectContainer;

public class Db4oDrsFixture implements DrsFixture {
	static final File RAM_DRIVE = new File("w:");
	
	protected String _name;
	protected ExtObjectContainer _db;
	protected TestableReplicationProviderInside _provider;
	protected final File testFile;
	
	public Db4oDrsFixture(String name) {
		_name = name;
		
		if (RAM_DRIVE.exists())
			testFile = new File(RAM_DRIVE.getPath() + "drs_cs_" + _name + ".yap");
		else	
			testFile = new File("drs_cs_" + _name + ".yap");
	}
	
	public TestableReplicationProviderInside provider() {
		return _provider;
	}

	public void clean() {
		testFile.delete();
	}

	public void close() {
		_provider.destroy();
		_db.close();
	}

	public ExtObjectContainer db() {
		return _db;
	}

	public void open() {
		//	Comment out because MemoryIoAdapter has problems on .net 
		//	MemoryIoAdapter memoryIoAdapter = new MemoryIoAdapter();
		//	Db4o.configure().io(memoryIoAdapter);
		
		_db = Db4o.openFile(testFile.getPath()).ext();
		_provider = Db4oProviderFactory.newInstance(_db, _name);
	}
}