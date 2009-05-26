/* Copyright (C) 2007  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.cs.internal.*;
import com.db4o.foundation.*;

import db4ounit.extensions.*;

public class ClientServerTestCaseBase extends Db4oClientServerTestCase {

	protected ServerMessageDispatcher serverDispatcher() {
		ObjectServerImpl serverImpl = (ObjectServerImpl) clientServerFixture().server();
		return (ServerMessageDispatcher)Iterators.next(serverImpl.iterateDispatchers());
	}
	
	protected ClientObjectContainer client(){
		return (ClientObjectContainer) db();
	}

}
