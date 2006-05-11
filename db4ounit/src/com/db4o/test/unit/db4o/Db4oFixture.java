package com.db4o.test.unit.db4o;

import com.db4o.ext.*;

public interface Db4oFixture {
	void open() throws Exception;
	void close() throws Exception;
	ExtObjectContainer db();
}
