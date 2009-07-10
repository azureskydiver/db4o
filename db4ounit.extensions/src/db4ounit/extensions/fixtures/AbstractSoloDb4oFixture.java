/* Copyright (C) 2004 - 2006 Versant Inc. http://www.db4o.com */

package db4ounit.extensions.fixtures;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.internal.*;

import db4ounit.Assert;
import db4ounit.extensions.*;

public abstract class AbstractSoloDb4oFixture extends AbstractDb4oFixture {

	private ExtObjectContainer _db;
	
	protected AbstractSoloDb4oFixture() {
	}
	
	public final void open(Db4oTestCase testInstance) {
		Assert.isNull(_db);
		final Configuration config = cloneConfiguration();
		applyFixtureConfiguration(testInstance, config);
		_db=createDatabase(config).ext();
	}

	public void close() throws Exception {
		if (null != _db) {
			Assert.isTrue(db().close());
			_db = null;
		}
	}	

	public boolean accept(Class clazz) {
		return !OptOutSolo.class.isAssignableFrom(clazz);
	}

	public ExtObjectContainer db() {
		return _db;
	}
	
	protected abstract ObjectContainer createDatabase(Configuration config);
	
	public LocalObjectContainer fileSession() {
		return (LocalObjectContainer)_db;
	}

	public void configureAtRuntime(RuntimeConfigureAction action) {
		action.apply(config());
	}

}