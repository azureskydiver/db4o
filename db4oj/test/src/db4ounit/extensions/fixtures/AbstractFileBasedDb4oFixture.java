/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.extensions.fixtures;

import java.io.File;


public abstract class AbstractFileBasedDb4oFixture extends AbstractDb4oFixture {
	
	private final File _yap;

	public AbstractFileBasedDb4oFixture(String fileName) {
		_yap = new File(fileName);
	}

	protected String getAbsolutePath() {
		return _yap.getAbsolutePath();
	}

	public void clean() {
		_yap.delete();
	}


}
