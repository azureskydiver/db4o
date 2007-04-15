package db4ounit.extensions;

import com.db4o.ext.*;

import db4ounit.*;

public interface Db4oTestCase extends TestCase, TestLifeCycle {
	ExtObjectContainer db() throws Exception;
}
