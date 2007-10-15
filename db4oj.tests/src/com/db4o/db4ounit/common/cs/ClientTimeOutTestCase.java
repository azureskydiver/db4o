/* Copyright (C) 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.events.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.messaging.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class ClientTimeOutTestCase extends Db4oClientServerTestCase implements OptOutAllButNetworkingCS{
    
    private static final int TIMEOUT = 500;
    
    static boolean _clientWasBlocked;
    
    static boolean _timeoutEventArrived;

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

	TestMessageRecipient recipient = new TestMessageRecipient();

	public void test() {
	    
	   ClientEventRegistryFactory.forClient(db()).clientSocketReadTimeout().addListener(new EventListener4() {
            public void onEvent(Event4 e, EventArgs args) {
                
                // For this test we simply continue on all timeouts.
                
                _timeoutEventArrived = true;
            }
	   });
       
       store(new Item("one"));

       clientServerFixture().server().ext().configure().clientServer()
				.setMessageRecipient(recipient);

       final ExtObjectContainer client = clientServerFixture().db();
       MessageSender sender = client.configure().clientServer()
				.getMessageSender();
       
       _timeoutEventArrived = false;
       _clientWasBlocked = false;
       sender.send(new Data());
       
       long start = System.currentTimeMillis();
       ObjectSet os = client.get(null);
       long stop = System.currentTimeMillis();
       long duration = stop - start;
       
       Assert.isGreater(TIMEOUT, duration);
       
       Assert.isGreater(0, os.size());
       
       Assert.isTrue(_clientWasBlocked);
       Assert.isTrue(_timeoutEventArrived);
       
	}

	public static class TestMessageRecipient implements MessageRecipient {
		public void processMessage(ObjectContainer con, Object message) {
			Cool.sleepIgnoringInterruption(1500);
			_clientWasBlocked = true;
		}
	}

	public static class Data {
	}
}