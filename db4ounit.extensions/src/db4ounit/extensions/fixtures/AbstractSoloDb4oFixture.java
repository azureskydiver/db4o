/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.extensions.fixtures;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;

public abstract class AbstractSoloDb4oFixture extends AbstractDb4oFixture {

	private ExtObjectContainer _db;
	
	protected AbstractSoloDb4oFixture(ConfigurationSource configSource) {
		super(configSource);
	}
	
	public final void open() {
		_db=createDatabase(config()).ext();
	}
	
	public void close() throws Exception {
		_db.close();
		_db = null;
	}

	public ExtObjectContainer db() {
		return _db;
	}
	
	protected abstract ObjectContainer createDatabase(Configuration config);
}