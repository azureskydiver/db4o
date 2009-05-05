/* Copyright (C) 2004 - 2006 Versant Inc. http://www.db4o.com */

package db4ounit.extensions.fixtures;

import java.io.*;


public abstract class AbstractFileBasedDb4oFixture extends AbstractSoloDb4oFixture {
	
	private final File _yap;

	public AbstractFileBasedDb4oFixture(ConfigurationSource configSource,String fileName) {
		super(configSource);
		_yap = new File(fileName);
	}

	public String getAbsolutePath() {
		return _yap.getAbsolutePath();
	}

	protected void doClean() {
		_yap.delete();
	}


}
