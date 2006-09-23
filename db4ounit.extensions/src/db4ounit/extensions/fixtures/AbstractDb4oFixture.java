/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.extensions.fixtures;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;

import db4ounit.extensions.*;

public abstract class AbstractDb4oFixture implements Db4oFixture {

	private ExtObjectContainer _db;
	private final ConfigurationSource _configSource;
	private Configuration _config;
	
	protected AbstractDb4oFixture(ConfigurationSource configSource) {
		_configSource=configSource;
	}
	
	public final void open() {
		_db=createDatabase(config()).ext();
	}
	
	public void close() throws Exception {
		_db.close();
		_db = null;
	}

	public void reopen() throws Exception {
		_db.close();
		open();
	}
	
	public ExtObjectContainer db() {
		return _db;
	}
	
	public Configuration config() {
		if(_config==null) {
			_config=_configSource.config();
		}
		return _config;
	}
	
	public void clean() {
		doClean();
		_config=null;
	}

	protected abstract void doClean();	
	
	protected abstract ObjectContainer createDatabase(Configuration config);
}