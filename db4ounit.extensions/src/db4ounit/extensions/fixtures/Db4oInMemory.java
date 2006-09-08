/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;

import com.db4o.ext.*;

public class Db4oInMemory extends AbstractDb4oFixture {
    
	private MemoryFile _memoryFile;
	
	public void open() {
		if (null == _memoryFile) {
			_memoryFile = new MemoryFile();
		}
		db(ExtDb4o.openMemoryFile(_memoryFile).ext());
	}

    public void clean() {
    	_memoryFile = null;
    }
}
