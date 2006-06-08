package db4ounit.db4o.fixtures;

import java.io.File;
import java.io.IOException;

import com.db4o.Db4o;
import com.db4o.ext.ExtObjectContainer;

import db4ounit.db4o.Db4oFixture;

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
