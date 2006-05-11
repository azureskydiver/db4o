package com.db4o.test.unit.db4o.fixtures;

import java.io.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.test.unit.db4o.*;

public class Db4oSolo implements Db4oFixture {
	private File _file;
	private ExtObjectContainer _db;
	
	public void open() throws IOException {
		_file=new File("db4otest.yap");
		_file.delete();
		_db=Db4o.openFile(_file.getPath()).ext();
	}

	public void close() {
		_db.close();
		_file.delete();
	}

	public ExtObjectContainer db() {
		return _db;
	}
}
