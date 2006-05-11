package com.db4o.test.unit.db4o.fixtures;

import com.db4o.ext.*;
import com.db4o.test.unit.db4o.*;

public class Db4oInMemory implements Db4oFixture {
	private ExtObjectContainer _db;
	
	public void open() {
		_db=ExtDb4o.openMemoryFile(new MemoryFile()).ext();
	}

	public void close() {
		_db.close();
	}

	public ExtObjectContainer db() {
		return _db;
	}
}
