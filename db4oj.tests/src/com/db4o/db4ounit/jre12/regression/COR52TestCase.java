/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */
/**
 * @sarpen.if !SILVERLIGHT
 */
package com.db4o.db4ounit.jre12.regression;

import com.db4o.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.internal.*;

import db4ounit.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class COR52TestCase extends TestWithTempFile {
	
	public static void main(String[] args) {
		new ConsoleTestRunner(COR52TestCase.class).run();
	}
	
	/**
	 * @deprecated using deprecated api
	 */
	public void test() throws Exception {
		int originalActivationDepth = ((Config4Impl) Db4o.configure())
				.activationDepth();
		Db4o.configure().activationDepth(0);
		ObjectServer server = Db4o.openServer(tempFile(), -1);
		try {
			server.grantAccess("db4o", "db4o");
			ObjectContainer oc = Db4o.openClient("localhost", server.ext().port(), "db4o",
					"db4o");
			oc.close();
		} finally {
			Db4o.configure().activationDepth(originalActivationDepth);
			server.close();
		}

	}
}
