/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.db4o.fixtures;

import com.db4o.ext.ExtDb4o;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.ext.MemoryFile;

import db4ounit.db4o.Db4oFixture;

public class Db4oInMemory implements Db4oFixture {
    
	private ExtObjectContainer _db;
	private MemoryFile _memoryFile;
	
	public void open() {
		if (null == _memoryFile) {
			_memoryFile = new MemoryFile();
		}
		_db=ExtDb4o.openMemoryFile(_memoryFile).ext();
	}

	public void close() {
		_db.close();
	}

    public void clean() {
    	_memoryFile = null;
    }
    
	public ExtObjectContainer db() {
		return _db;
	}

}
