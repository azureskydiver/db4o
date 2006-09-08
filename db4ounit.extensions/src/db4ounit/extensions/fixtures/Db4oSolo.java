/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;

import java.io.IOException;

import com.db4o.Db4o;

public class Db4oSolo extends AbstractFileBasedDb4oFixture {

	public Db4oSolo() {
		super("db4oSoloTest.yap");	
	}
    
	public void open() throws IOException {
		db(Db4o.openFile(getAbsolutePath()).ext());
	}
}
