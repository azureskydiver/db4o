/* Copyright (C) 2007 Versant Inc. http://www.db4o.com */
package com.db4o.db4ounit.common.exceptions;

import com.db4o.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class InvalidPasswordTestCase
	extends Db4oClientServerTestCase
	implements OptOutAllButNetworkingCS {

	public static void main(String[] args) {
		new InvalidPasswordTestCase().runAll();
	}
	
	public void testInvalidPassword() {
		final int port = clientServerFixture().serverPort();
		Assert.expect(InvalidPasswordException.class, new CodeBlock() {
			public void run() throws Throwable {
				Db4o.openClient("127.0.0.1", port, "strangeusername",
						"invalidPassword");
			}
		});
	}
	
	public void testEmptyUserPassword() {
		final int port = clientServerFixture().serverPort();
		Assert.expect(InvalidPasswordException.class, new CodeBlock() {
			public void run() throws Throwable {
				Db4o.openClient("127.0.0.1", port, "", "");
			}
		});
	}
	
	public void testEmptyUserNullPassword() {
		final int port = clientServerFixture().serverPort();
		Assert.expect(InvalidPasswordException.class, new CodeBlock() {
			public void run() throws Throwable {
				Db4o.openClient("127.0.0.1", port, "", null);
			}
		});
	}
	
	public void testNullPassword() {
		final int port = clientServerFixture().serverPort();
		Assert.expect(InvalidPasswordException.class, new CodeBlock() {
			public void run() throws Throwable {
				Db4o.openClient("127.0.0.1", port, null, null);
			}
		});
	}
}
