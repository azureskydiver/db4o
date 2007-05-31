/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.foundation.Iterator4;
import com.db4o.internal.cs.*;

import db4ounit.extensions.Db4oClientServerTestCase;


public class ClientServerTestCaseBase extends Db4oClientServerTestCase {

	protected ServerMessageDispatcher serverDispatcher() {
		ObjectServerImpl serverImpl = (ObjectServerImpl) clientServerFixture().server();
		Iterator4 iter = serverImpl.iterateDispatchers();
		iter.moveNext();
		ServerMessageDispatcher dispatcher = (ServerMessageDispatcher) iter.current();
		return dispatcher;
	}
	
	protected ClientObjectContainer client(){
		return (ClientObjectContainer) db();
	}

}
