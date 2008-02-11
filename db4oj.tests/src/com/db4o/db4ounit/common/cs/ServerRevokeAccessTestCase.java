/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import java.io.*;

import com.db4o.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class ServerRevokeAccessTestCase
	extends Db4oClientServerTestCase
	implements OptOutAllButNetworkingCS {

	private static final String SERVER_HOSTNAME = "127.0.0.1";

	public static void main(String[] args) {
		new ServerRevokeAccessTestCase().runAll();
	}

	/**
	 * @sharpen.if !CF
	 */
	public void test() throws IOException {
		final String user = "hohohi";
		final String password = "hohoho";
		ObjectServer server = clientServerFixture().server();
		server.grantAccess(user, password);

		ObjectContainer con = Db4o.openClient(SERVER_HOSTNAME,
				clientServerFixture().serverPort(), user, password);
		Assert.isNotNull(con);
		con.close();

		server.ext().revokeAccess(user);

		Assert.expect(Exception.class, new CodeBlock() {
			public void run() throws Throwable {
				Db4o.openClient(SERVER_HOSTNAME, clientServerFixture()
						.serverPort(), user, password);
			}
		});
	}
}
