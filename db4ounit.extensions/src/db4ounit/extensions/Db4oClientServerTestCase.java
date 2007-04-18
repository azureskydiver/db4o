/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package db4ounit.extensions;

import com.db4o.ext.*;

import db4ounit.extensions.concurrency.*;
import db4ounit.extensions.fixtures.*;

public class Db4oClientServerTestCase extends AbstractDb4oTestCase implements OptOutSolo {
	
	public Db4oClientServerFixture clientServerFixture() {
		return (Db4oClientServerFixture) fixture();
	}
	
	public ExtObjectContainer openNewClient() {
		return clientServerFixture().openNewClient();
	}
	
	public static int threadCount() {
		return ConcurrenyConst.CONCURRENCY_THREAD_COUNT;
	}
}
