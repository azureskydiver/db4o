/* Copyright (C) 2009  Versant Inc.  http://www.db4o.com */
package com.db4o.db4ounit.common.io;

import com.db4o.db4ounit.common.api.*;
import com.db4o.io.*;

import db4ounit.fixtures.*;

public class IoAdapterTestUnitBase extends TestWithTempFile {

	protected IoAdapter _adapter;

	public IoAdapterTestUnitBase() {
		super();
	}

	public void setUp() throws Exception {
		open(false);
    }

	protected void open(final boolean readOnly) {
		if (null != _adapter) {
			throw new IllegalStateException();
		}
	    _adapter = factory().open(tempFile(), false, 0, readOnly);
    }

	public void tearDown() throws Exception {
    	close();
    	super.tearDown();
    }

	protected void close() {
	    if (null != _adapter) {
    		_adapter.close();
    		_adapter = null;
    	}
    }

	private IoAdapter factory() {
    	return SubjectFixtureProvider.value();
    }

}