/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package db4ounit.extensions;

import com.db4o.cs.events.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.internal.cs.*;

import db4ounit.extensions.fixtures.*;

public class Db4oClientServerTestCase extends AbstractDb4oTestCase implements OptOutSolo {
	
	public Db4oClientServerFixture clientServerFixture() {
		return (Db4oClientServerFixture) fixture();
	}
	
	public ExtObjectContainer openNewClient() {
		return clientServerFixture().openNewClient();
	}
	
	protected void closeOnTimeouts(ClientObjectContainer client){
	    ClientEventRegistryFactory.forClient(client).clientSocketReadTimeout().addListener(new EventListener4() {
            public void onEvent(Event4 e, EventArgs args) {
                CancellableEventArgs cancellableArgs = (CancellableEventArgs) args;
                cancellableArgs.cancel();
            }
        });
    }

	
}
