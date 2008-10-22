/* Copyright (C) 2004 - 20067 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.db4ounit.common.util.*;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runAll();
    }
	
	protected Class[] testCases() {
		Class[] commonCases = {
		        CallConstructorsConfigTestCase.class,
		        ClientDisconnectTestCase.class,
	            ClientTimeOutTestCase.class,
	            ClientTransactionHandleTestCase.class,
	            ClientTransactionPoolTestCase.class,
	            CloseServerBeforeClientTestCase.class,
	            DeleteReaddTestCase.class,
	            IsAliveTestCase.class,
	            NoTestConstructorsQEStringCmpTestCase.class,
	            ObjectServerTestCase.class,
	            PrimitiveMessageTestCase.class,
	            SendMessageToClientTestCase.class,
	            ServerClosedTestCase.class,
	            ServerPortUsedTestCase.class,
	            ServerRevokeAccessTestCase.class,
	            ServerTimeoutTestCase.class,
	            ServerToClientTestCase.class,
	            SetSemaphoreTestCase.class,
	            SwitchingFilesFromClientTestCase.class,
	            SwitchingFilesFromMultipleClientsTestCase.class,
		};
		return Db4oUnitTestUtil.mergeClasses(commonCases, nonDecafTestCases());
	}

	/**
	 * @decaf.replaceFirst return new Class[0];
	 */
	private Class[] nonDecafTestCases() {
		return new Class[] {
	            CsSchemaUpdateTestCase.class,
		};
	}
	
}
