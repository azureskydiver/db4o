/* Copyright (C) 2004 - 2006 Versant Inc. http://www.db4o.com */

package db4ounit.extensions.fixtures;

import java.io.*;


public abstract class AbstractFileBasedDb4oFixture extends AbstractSoloDb4oFixture {
	
	private final File _databaseFile;

	public AbstractFileBasedDb4oFixture(String fileName) {
		_databaseFile = new File(fileName);
	}

	public String getAbsolutePath() {
		return _databaseFile.getAbsolutePath();
	}

	protected void doClean() {
		_databaseFile.delete();
	}


}
