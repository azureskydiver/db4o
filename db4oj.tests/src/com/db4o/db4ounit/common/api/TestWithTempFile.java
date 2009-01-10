package com.db4o.db4ounit.common.api;

import com.db4o.foundation.io.*;

import db4ounit.*;

public class TestWithTempFile implements TestLifeCycle{

	private final String _tempFile = Path4.getTempFileName();
	
	protected String tempFile() {
		return _tempFile;
	}

	public void setUp() throws Exception {
	}

	public void tearDown() throws Exception {
		File4.delete(tempFile());
	}

}