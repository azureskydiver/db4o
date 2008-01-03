/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.performance;

import java.io.*;
import com.db4o.*;

public class TransactionsPerSecond {
    
    public static void main(String[] args) {
        new TransactionsPerSecond().run();
    }
    
    public static class Item{
        public int _int;
        public Item(){
        }
        public Item(int int_){
            _int = int_;
        }
    }
    
    private static final String FILE = "tps.db4o";
    
    private static final long TOTAL_COUNT = 5000;
    
    public void run(){
        
        // This switch will make a big difference:
        Db4o.configure().flushFileBuffers(false);
        
        new File(FILE).delete();
        
        ObjectContainer objectContainer = Db4o.openFile(FILE).ext();
        
        long start = System.currentTimeMillis();
        
        for (int i = 0; i < TOTAL_COUNT; i++) {
            objectContainer.store(new Item(i));
            objectContainer.commit();
        }
        
        long stop = System.currentTimeMillis();
        long duration = stop - start;
        objectContainer.close();
        
        System.out.println("Time to store " + TOTAL_COUNT + " objects: " + duration + "ms");
        
        double seconds = ((double)duration) / ((double)1000); 
        double tps = TOTAL_COUNT / seconds;
        
        System.out.println("Transactions per second: " + tps);
    }

}
