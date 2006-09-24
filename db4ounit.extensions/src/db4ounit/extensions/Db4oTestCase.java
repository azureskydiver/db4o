package db4ounit.extensions;

import com.db4o.ext.ExtObjectContainer;

import db4ounit.TestCase;
import db4ounit.TestLifeCycle;

public interface Db4oTestCase extends TestCase, TestLifeCycle {

	/**
	 * returns an ExtObjectContainer as a parameter for test method.
	 * 
	 * @return ExtObjectContainer
	 */
	public ExtObjectContainer db();

}