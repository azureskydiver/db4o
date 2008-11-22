package com.db4o.db4ounit.common.io;

import com.db4o.db4ounit.common.api.*;
import com.db4o.io.*;

import db4ounit.fixtures.*;

public class StorageTestUnitBase extends TestWithTempFile {

	protected StorageFactory.Bin _storage;

	public StorageTestUnitBase() {
		super();
	}

	@Override
	public void setUp() throws Exception {
    	super.setUp();
    	open(false);
    }

	protected void open(final boolean readOnly) {
		if (null != _storage) {
			throw new IllegalStateException();
		}
	    _storage = factory().open(_tempFile, false, 0, readOnly);
    }

	@Override
	public void tearDown() throws Exception {
    	close();
    	super.tearDown();
    }

	protected void close() {
	    if (null != _storage) {
    		_storage.close();
    		_storage = null;
    	}
    }

	private StorageFactory factory() {
    	return SubjectFixtureProvider.value();
    }

}