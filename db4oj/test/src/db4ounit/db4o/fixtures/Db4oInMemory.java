package db4ounit.db4o.fixtures;

import com.db4o.ext.ExtDb4o;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.ext.MemoryFile;

import db4ounit.db4o.Db4oFixture;

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
