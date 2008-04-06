/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;

import com.db4o.config.*;
import com.db4o.defragment.*;

import db4ounit.extensions.*;

public abstract class AbstractDb4oFixture implements Db4oFixture {

	private final ConfigurationSource _configSource;
	private Configuration _config;
	private FixtureConfiguration _fixtureConfiguration;

	protected AbstractDb4oFixture(ConfigurationSource configSource) {
		_configSource=configSource;
	}
	
	public void fixtureConfiguration(FixtureConfiguration fc) {
		_fixtureConfiguration = fc;
	}
	
	public void reopen(Class testCaseClass) throws Exception {
		close();
		open(testCaseClass);
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
	
	public abstract boolean accept(Class clazz);

	protected abstract void doClean();	
	
	protected void resetConfig() {
		_config=null;
	}
	
	protected void defragment(String fileName) throws Exception{
        String targetFile = fileName + ".defrag.backup";
        DefragmentConfig defragConfig = new DefragmentConfig(fileName, targetFile);
        defragConfig.forceBackupDelete(true);
        defragConfig.db4oConfig(config());
		com.db4o.defragment.Defragment.defrag(defragConfig);
	}
	
	protected String buildLabel(String label) {
		if (null == _fixtureConfiguration) return label;
		return label + " - " + _fixtureConfiguration.getLabel();
	}

	protected void applyFixtureConfiguration(Class testCaseClass, final Configuration config) {
		if (null == _fixtureConfiguration) return;
		_fixtureConfiguration.configure(testCaseClass, config);
	}
	
	public String toString() {
		return label();
	}
}
