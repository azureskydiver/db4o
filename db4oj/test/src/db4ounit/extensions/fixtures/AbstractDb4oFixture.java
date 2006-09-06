/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.extensions.fixtures;

import com.db4o.ext.ExtObjectContainer;

import db4ounit.Assert;
import db4ounit.extensions.Db4oFixture;

public abstract class AbstractDb4oFixture implements Db4oFixture {

	private ExtObjectContainer _db;
	
	public void close() throws Exception {
		_db.close();
		_db = null;
	}
	
	public ExtObjectContainer db() {
		return _db;
	}
	
	protected void db(ExtObjectContainer container) {
		Assert.isNull(_db);
		_db = container;
	}
}