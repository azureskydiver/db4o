/* Copyright (C) 2006  Versant Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.defragment.*;
import com.db4o.internal.*;

import db4ounit.extensions.*;

public abstract class AbstractDb4oFixture implements Db4oFixture {

	private FixtureConfiguration _fixtureConfiguration;
	private Configuration _configuration;

	protected AbstractDb4oFixture() {
		resetConfig();
	}
	
	public void fixtureConfiguration(FixtureConfiguration fc) {
		_fixtureConfiguration = fc;
	}
	
	public void reopen(Db4oTestCase testInstance) throws Exception {
		close();
		open(testInstance);
	}

	public Configuration config() {
		return _configuration;
	}
	
	public void clean() {
		doClean();
		resetConfig();
	}
	
	public abstract boolean accept(Class clazz);

	protected abstract void doClean();	
	
	public void resetConfig() {
		_configuration = newConfiguration();
	}

	/**
	 * Method can be overridden in subclasses with special instantiation requirements (oSGI for instance).
	 * 
	 * @return
	 */
	protected Configuration newConfiguration() {
	    return Db4o.newConfiguration();
    }
	
	protected void defragment(String fileName) throws Exception{
        String targetFile = fileName + ".defrag.backup";
        DefragmentConfig defragConfig = new DefragmentConfig(fileName, targetFile);
        defragConfig.forceBackupDelete(true);
        defragConfig.db4oConfig(cloneConfiguration());
		com.db4o.defragment.Defragment.defrag(defragConfig);
	}
	
	protected String buildLabel(String label) {
		if (null == _fixtureConfiguration) return label;
		return label + " - " + _fixtureConfiguration.getLabel();
	}

	protected void applyFixtureConfiguration(Db4oTestCase testInstance, final Configuration config) {
		if (null == _fixtureConfiguration) return;
		_fixtureConfiguration.configure(testInstance, config);
	}
	
	public String toString() {
		return label();
	}

	protected Config4Impl cloneConfiguration() {
        return cloneDb4oConfiguration((Config4Impl) config());
    }

	protected Config4Impl cloneDb4oConfiguration(Configuration config) {
    	return (Config4Impl) ((Config4Impl)config).deepClone(this);
    }
}
