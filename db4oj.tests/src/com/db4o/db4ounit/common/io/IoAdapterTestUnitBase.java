package com.db4o.db4ounit.common.io;

import java.io.*;

import com.db4o.foundation.io.*;
import com.db4o.io.*;

import db4ounit.*;
import db4ounit.fixtures.*;

public class IoAdapterTestUnitBase implements TestLifeCycle {

	private final String _filename = Path4.getTempFileName();
	protected IoAdapter _adapter;

	public IoAdapterTestUnitBase() {
		super();
	}

	public void setUp() throws Exception {
    	deleteTestFile();
    	open(false);
    }

	protected void open(final boolean readOnly) {
		if (null != _adapter) {
			throw new IllegalStateException();
		}
	    _adapter = factory().open(_filename, false, 0, readOnly);
    }

	public void tearDown() throws Exception {
    	close();
    	deleteTestFile();
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

	private void deleteTestFile() throws Exception {
    	new File(_filename).delete();
    }

}