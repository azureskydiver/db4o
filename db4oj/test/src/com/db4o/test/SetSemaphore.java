/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ext.*;
import com.db4o.foundation.*;

public class SetSemaphore {

    public void test() {

        final ExtObjectContainer client1 = Test.objectContainer();

        Test.ensure(client1.setSemaphore("hi", 0));
        Test.ensure(client1.setSemaphore("hi", 0));

        if (Test.clientServer) {
            final ExtObjectContainer client2 = Test.open();
            final ExtObjectContainer client3 = Test.open();
            final ExtObjectContainer client4 = Test.open();
            final ExtObjectContainer client5 = Test.open();

            Test.ensure(!client2.setSemaphore("hi", 0));
            client1.releaseSemaphore("hi");
            Test.ensure(client2.setSemaphore("hi", 50));
            Test.ensure(!client1.setSemaphore("hi", 0));
            Test.ensure(!client3.setSemaphore("hi", 0));
            
            new GetAndRelease(client3);
            new GetAndRelease(client2);
            new GetAndRelease(client1);
            new GetAndRelease(client4);
            new GetAndRelease(client5);
            
            Cool.sleepIgnoringInterruption(1000);
            Test.ensure(client1.setSemaphore("hi", 0));
            client1.close();
            
            new GetAndRelease(client3);
            new GetAndRelease(client2);
            Cool.sleepIgnoringInterruption(1000);
            
            client2.close(); 
            client3.close(); // the last one opened remains
            client4.close(); // open for other tests
            
            client5.setSemaphore("hi", 1000);
        }

    }

    static class GetAndRelease implements Runnable {

        ExtObjectContainer client;

        public GetAndRelease(ExtObjectContainer client) {
            this.client = client;
            new Thread(this).start();
        }

        public void run() {
            long time = System.currentTimeMillis();
            Test.ensure(client.setSemaphore("hi", 50000));
            time = System.currentTimeMillis() - time;
            // System.out.println("Time to get semaphore: " + time);
            Cool.sleepIgnoringInterruption(50);

            // System.out.println("About to release semaphore.");
            client.releaseSemaphore("hi");
        }
    }

}
