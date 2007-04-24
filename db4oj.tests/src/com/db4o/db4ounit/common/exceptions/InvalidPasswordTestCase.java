/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.db4ounit.common.exceptions;

import com.db4o.Db4o;
import com.db4o.internal.cs.InvalidPasswordException;

import db4ounit.Assert;
import db4ounit.CodeBlock;
import db4ounit.extensions.Db4oClientServerTestCase;

public class InvalidPasswordTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new InvalidPasswordTestCase().runClientServer();
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
	
	public void testEmptyPassword() {
		final int port = clientServerFixture().serverPort();
		Assert.expect(InvalidPasswordException.class, new CodeBlock() {
			public void run() throws Throwable {
				Db4o.openClient("127.0.0.1", port, "", "");
			}
		});
		
		Assert.expect(InvalidPasswordException.class, new CodeBlock() {
			public void run() throws Throwable {
				Db4o.openClient("127.0.0.1", port, "", null);
			}
		});
		
		Assert.expect(InvalidPasswordException.class, new CodeBlock() {
			public void run() throws Throwable {
				Db4o.openClient("127.0.0.1", port, null, null);
			}
		});
	}
}
