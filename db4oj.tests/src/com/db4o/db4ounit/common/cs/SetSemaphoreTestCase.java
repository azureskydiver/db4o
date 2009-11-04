/* Copyright (C) 2007  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.ext.*;
import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class SetSemaphoreTestCase extends Db4oClientServerTestCase implements OptOutSolo {

    private static final String SEMAPHORE_NAME = "hi";

	public static void main(String[] args) {
		new SetSemaphoreTestCase().runAll();
    }

    public void testSemaphoreReentrancy() {
        ExtObjectContainer container = db();
		
        Assert.isTrue(container.setSemaphore(SEMAPHORE_NAME, 0));
		Assert.isTrue(container.setSemaphore(SEMAPHORE_NAME, 0));
		
		container.releaseSemaphore(SEMAPHORE_NAME);
    }
    
    public void testOwnedSemaphoreCannotBeTaken() {
        ExtObjectContainer client1 = openNewSession();
        
        try {
	        Assert.isTrue(db().setSemaphore(SEMAPHORE_NAME, 0));
	        Assert.isFalse(client1.setSemaphore(SEMAPHORE_NAME, 0));
        }
        finally {
        	client1.close();
        }
    }
    
    public void testPreviouslyOwnedSemaphoreCannotBeTaken() {
        ExtObjectContainer client1 = openNewSession();
        
        try {
	        Assert.isTrue(db().setSemaphore(SEMAPHORE_NAME, 0));
	        Assert.isFalse(client1.setSemaphore(SEMAPHORE_NAME, 0));
	        
	        db().releaseSemaphore(SEMAPHORE_NAME);
	        Assert.isTrue(client1.setSemaphore(SEMAPHORE_NAME, 0));
	        Assert.isFalse(db().setSemaphore(SEMAPHORE_NAME, 0));
        }
        finally {
        	client1.close();
        }
    }
    
    public void testClosingClientReleasesSemaphores() {
    	ExtObjectContainer client1 = openNewSession();
        
	    Assert.isTrue(client1.setSemaphore(SEMAPHORE_NAME, 0));
	    Assert.isFalse(db().setSemaphore(SEMAPHORE_NAME, 0));
	        
	    client1.close();
	        
	    Assert.isTrue(db().setSemaphore(SEMAPHORE_NAME, 0));
    }
    
	public void testMultipleThreads() throws InterruptedException {

        final ExtObjectContainer[] clients = new ExtObjectContainer[5];

        clients[0] = db();
        for (int i = 1; i < clients.length; i++) {
            clients[i] = openNewSession();
        }
        
        Assert.isTrue(clients[1].setSemaphore(SEMAPHORE_NAME, 50));
        Thread[] threads = new Thread[clients.length];

        for (int i = 0; i < clients.length; i++) {
            threads[i] = startGetAndReleaseThread(clients[i]);
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
        
        ensureMessageProcessed(clients[0]);

        Assert.isTrue(clients[0].setSemaphore(SEMAPHORE_NAME, 0));
        clients[0].close();
        

        threads[2] = startGetAndReleaseThread(clients[2]);
        threads[1] = startGetAndReleaseThread(clients[1]);

        threads[1].join();
        threads[2].join();

        for (int i = 1; i < clients.length - 1; i++) {
            clients[i].close();
        }

        clients[4].setSemaphore(SEMAPHORE_NAME, 1000);
        clients[4].close();
    }

    private Thread startGetAndReleaseThread(ExtObjectContainer client) {
        Thread t = new Thread(new GetAndRelease(client));
        t.start();
        return t;
    }

	private static void ensureMessageProcessed(ExtObjectContainer client) {
		client.commit();
		Cool.sleepIgnoringInterruption(50);
	}

    static class GetAndRelease implements Runnable {

        private ExtObjectContainer _client;

        public GetAndRelease(ExtObjectContainer client) {
            _client = client;
        }

        public void run() {
	        Assert.isTrue(_client.setSemaphore(SEMAPHORE_NAME, 50000));

        	ensureMessageProcessed(_client);
            _client.releaseSemaphore(SEMAPHORE_NAME);
        }
     }
}
