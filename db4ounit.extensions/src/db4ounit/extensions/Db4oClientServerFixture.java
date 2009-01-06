/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package db4ounit.extensions;

import com.db4o.*;
import com.db4o.ext.*;

public interface Db4oClientServerFixture extends Db4oFixture {
	
	public ObjectServer server();
	
	public int serverPort();
	
	public ExtObjectContainer openNewClient();
	
	public boolean embeddedClients();
}
