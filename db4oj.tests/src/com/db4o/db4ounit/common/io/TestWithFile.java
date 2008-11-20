package com.db4o.db4ounit.common.io;

import java.io.*;

import com.db4o.foundation.io.*;

import db4ounit.*;

public class TestWithFile implements TestLifeCycle {

	protected final String _filename = Path4.getTempFileName();

	public TestWithFile() {
		super();
	}

	protected void deleteTestFile() throws Exception {
    	new File(_filename).delete();
    }

	public void setUp() throws Exception {
		deleteTestFile();
    }

	public void tearDown() throws Exception {
    	deleteTestFile();
    }

}