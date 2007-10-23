/* Copyright (C) 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.messaging.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class ClientTimeOutTestCase extends Db4oClientServerTestCase implements OptOutAllButNetworkingCS{
    
    private static final int TIMEOUT = 500;
    
    static boolean _clientWasBlocked;
    
    TestMessageRecipient recipient = new TestMessageRecipient();
    
	public static void main(String[] args) {
		new ClientTimeOutTestCase().runAll();
	}
	
	public static class Item{
	    
	    public String _name;
	    
	    public Item(String name){
	        _name = name;
	    }
	    
	}

	protected void configure(Configuration config) {
		config.clientServer().timeoutClientSocket(TIMEOUT);
	}
	
	public void testKeptAliveClient(){
	    Item item = new Item("one");
        store(item);
	    Cool.sleepIgnoringInterruption(TIMEOUT * 2);
	    Assert.areSame(item, retrieveOnlyInstance(Item.class));
	}
	

	public void testTimedoutAndClosedClient() {
       store(new Item("one"));
       clientServerFixture().server().ext().configure().clientServer()
				.setMessageRecipient(recipient);
       final ExtObjectContainer client = clientServerFixture().db();
       MessageSender sender = client.configure().clientServer()
				.getMessageSender();
       _clientWasBlocked = false;
       sender.send(new Data());
       long start = System.currentTimeMillis();
       Assert.expect(DatabaseClosedException.class, new CodeBlock() {
           public void run() throws Throwable {
               client.get(null);
           }
       });
       long stop = System.currentTimeMillis();
       long duration = stop - start;
       Assert.isGreaterOrEqual(TIMEOUT / 2, duration);
       Assert.isTrue(_clientWasBlocked);
	}

	public static class TestMessageRecipient implements MessageRecipient {
		public void processMessage(ObjectContainer con, Object message) {
            _clientWasBlocked = true;
			Cool.sleepIgnoringInterruption(TIMEOUT * 3);
		}
	}

	public static class Data {
	}
}