/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.ext.*;
import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class SetSemaphoreTestCase extends Db4oClientServerTestCase implements OptOutSolo {

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new SetSemaphoreTestCase().runClientServer();
        }
    }

    public void _test() throws InterruptedException {

        final ExtObjectContainer[] clients = new ExtObjectContainer[5];

        clients[0] = db();

        Assert.isTrue(clients[0].setSemaphore("hi", 0));
        Assert.isTrue(clients[0].setSemaphore("hi", 0));

        for (int i = 1; i < clients.length; i++) {
            clients[i] = openNewClient();
        }

        Assert.isFalse(clients[1].setSemaphore("hi", 0));
        clients[0].releaseSemaphore("hi");
        Assert.isTrue(clients[1].setSemaphore("hi", 50));
        Assert.isFalse(clients[0].setSemaphore("hi", 0));
        Assert.isFalse(clients[2].setSemaphore("hi", 0));

        Thread[] threads = new Thread[clients.length];

        for (int i = 0; i < clients.length; i++) {
            threads[i] = startGetAndReleaseThread(clients[i]);
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
        
        ensureMessageProcessed(clients[0]);

        Assert.isTrue(clients[0].setSemaphore("hi", 0));
        clients[0].close();

        threads[2] = startGetAndReleaseThread(clients[2]);
        threads[1] = startGetAndReleaseThread(clients[1]);

        threads[1].join();
        threads[2].join();

        for (int i = 1; i < 4; i++) {
            clients[i].close();
        }

        clients[4].setSemaphore("hi", 1000);

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

        ExtObjectContainer _client;

        public GetAndRelease(ExtObjectContainer client) {
            this._client = client;
        }

        public void run() {
            long time = System.currentTimeMillis();
            Assert.isTrue(_client.setSemaphore("hi", 50000));
            time = System.currentTimeMillis() - time;
            // System.out.println("Time to get semaphore: " + time);

            ensureMessageProcessed(_client);

            // System.out.println("About to release semaphore.");
            _client.releaseSemaphore("hi");
        }
     }

}
