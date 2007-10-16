/* Copyright (C) 2004 - 2007 db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.cs.events.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.internal.cs.*;

import db4ounit.extensions.*;

public class CloseServerBeforeClientTestCase extends Db4oClientServerTestCase {

	public static void main(String[] arguments) {
		new CloseServerBeforeClientTestCase().runClientServer();
	}

	public void test() throws Exception {
		ExtObjectContainer client = openNewClient();
		if(! isMTOC()){
    		closeOnTimeouts((ClientObjectContainer) db());
    		closeOnTimeouts((ClientObjectContainer) client);
		}
		try {
			clientServerFixture().server().close();
		} finally {
			try{
				client.close();
			} catch(Db4oException e) {
				// database may have been closed
			}
			
			try{
				fixture().close();
			} catch(Db4oException e) {
				// database may have been closed
			}
		}

	}

    private void closeOnTimeouts(ClientObjectContainer client) {
        ClientEventRegistryFactory.forClient(client).clientSocketReadTimeout().addListener(
            new EventListener4() {
                public void onEvent(Event4 e, EventArgs args) {
                    CancellableEventArgs cancellableArgs = (CancellableEventArgs) args;
                    cancellableArgs.cancel();
                }
            });
    }
	

}
