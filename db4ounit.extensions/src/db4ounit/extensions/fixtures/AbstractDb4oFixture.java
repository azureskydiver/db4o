/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;

import com.db4o.config.*;

import db4ounit.extensions.*;

public abstract class AbstractDb4oFixture implements Db4oFixture {

	private final ConfigurationSource _configSource;
	private Configuration _config;

	protected AbstractDb4oFixture(ConfigurationSource configSource) {
		_configSource=configSource;
	}
	
	public void reopen() throws Exception {
		close();
		open();
	}

	public Configuration config() {
		if(_config==null) {
			_config=_configSource.config();
		}
		return _config;
	}
	
	public void clean() {
		doClean();
		resetConfig();
	}

	protected abstract void doClean();	
	
	protected void resetConfig() {
		_config=null;
	}
}
