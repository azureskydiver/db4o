/* Copyright (C) 2007  Versant Inc.  http://www.db4o.com */

package com.db4o.test.performance;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;

public class TransactionsPerSecond {
    
    public static void main(String[] args) {
    	new TransactionsPerSecond().run(pointerBasedIdSystem());
    	new TransactionsPerSecond().run(stackedBTreeSystem());
    	new TransactionsPerSecond().run(singleBTreeSystem());
    }

	private static EmbeddedConfiguration pointerBasedIdSystem() {
		System.out.println("PointerBasedIdSystem");
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
    	config.idSystem().usePointerBasedSystem();
		return config;
	}
	
	private static EmbeddedConfiguration stackedBTreeSystem() {
		System.out.println("StackedBTreeSystem");
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
    	config.idSystem().useStackedBTreeSystem();
		return config;
	}
	
	private static EmbeddedConfiguration singleBTreeSystem() {
		System.out.println("SingleBTreeSystem");
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
    	config.idSystem().useSingleBTreeSystem();
		return config;
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
    
    public void run(EmbeddedConfiguration config){
        
        new File(FILE).delete();
        
        ObjectContainer objectContainer = Db4oEmbedded.openFile(config, FILE).ext();
        
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
