/* Copyright (C) 2007  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.util;

import db4ounit.*;



public class ThreadServices {
    
    public static void spawnAndJoin(final int threadCount, final CodeBlock codeBlock) throws InterruptedException {
        Thread[] threads = new Thread[threadCount]; 
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new Runnable() {
                public void run() {
                    try {
                        codeBlock.run();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            });
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
    }

}
