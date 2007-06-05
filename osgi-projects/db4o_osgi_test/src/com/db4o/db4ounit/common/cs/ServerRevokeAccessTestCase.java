/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
package com.db4o.db4ounit.common.cs;

import java.io.*;

import com.db4o.*;
import com.db4o.foundation.io.*;

import db4ounit.*;

public class ServerRevokeAccessTestCase implements TestCase {
	
	static final String FILE = "ServerRevokeAccessTest.yap";
	
	static final int SERVER_PORT = 0xdb42;
	
	static final String SERVER_HOSTNAME = "localhost";

	/**
	 * @sharpen.if !CF_1_0 && !CF_2_0
	 */
	public void test() throws IOException {
		File4.delete(FILE);		
		ObjectServer server = Db4o.openServer(FILE, SERVER_PORT);
		try {	
			final String user = "hohohi";
			final String password = "hohoho";
			server.grantAccess(user, password);
		
			ObjectContainer con = Db4o.openClient(SERVER_HOSTNAME, SERVER_PORT, user, password);
			Assert.isNotNull(con);
			con.close();
			
			server.ext().revokeAccess(user);
			
			Assert.expect(Exception.class, new CodeBlock() {
				public void run() throws Throwable {
					Db4o.openClient(SERVER_HOSTNAME, SERVER_PORT, user, password);
				}
			});
		} finally {
			server.close();
		}
	}	
}
