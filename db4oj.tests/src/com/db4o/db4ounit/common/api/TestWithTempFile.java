package com.db4o.db4ounit.common.api;

import com.db4o.foundation.io.*;

import db4ounit.*;

public class TestWithTempFile implements TestLifeCycle{

	protected final String _tempFile = Path4.getTempFileName();
	
	public void setUp() throws Exception {
	}

	public void tearDown() throws Exception {
		File4.delete(_tempFile);
	}

}