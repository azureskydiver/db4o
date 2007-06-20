/* Copyright (C) 2004 - 20067 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runClientServer();
    }

	protected Class[] testCases() {
		return new Class[] {
            ClientServerPingTestCase.class,
            ClientTimeOutTestCase.class,
            PingTestCase.class,
            SendMessageToClientTestCase.class,
            ServerPortUsedTestCase.class,
            ServerRevokeAccessTestCase.class,
            ServerTimeoutTestCase.class,
            CallConstructorsConfigTestCase.class,
		};
	}
}
