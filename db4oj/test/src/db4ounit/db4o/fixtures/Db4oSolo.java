/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.db4o.fixtures;

import java.io.File;
import java.io.IOException;

import com.db4o.Db4o;
import com.db4o.ext.ExtObjectContainer;

import db4ounit.db4o.Db4oFixture;

public class Db4oSolo implements Db4oFixture {
    
    public static final String FILENAME = "db4oSoloTest.yap"; 
    
	private ExtObjectContainer _db;
	
	public void open() throws IOException {
		_db=Db4o.openFile(new File(FILENAME).getPath()).ext();
	}

	public void close() {
		_db.close();
	}
    
    public void clean() {
        new File(FILENAME).delete();
    }

	public ExtObjectContainer db() {
		return _db;
	}
}
