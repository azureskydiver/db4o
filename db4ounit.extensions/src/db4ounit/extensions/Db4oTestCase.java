package db4ounit.extensions;

import com.db4o.ext.ExtObjectContainer;

import db4ounit.TestCase;
import db4ounit.TestLifeCycle;

public interface Db4oTestCase extends TestCase, TestLifeCycle {

	public void fixture(Db4oFixture fixture);

	public Db4oFixture fixture();

	public ExtObjectContainer db();

}