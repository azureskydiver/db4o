/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.io.*;

import com.db4o.*;
import com.db4o.query.*;

/**
 * 
 */
public class IndexedFieldPerformance implements Serializable {

    public String           index;

    public static final int SIZE = 50000;
    public static final int COMMIT_STEP = 5000;
    public static final int QUERIES = 1000;
    public static final String FILE = "ifp.yap"; 
    
    public static void main(String[] args){
        new Thread(new Runnable() {
            public void run() {
                Db4o.configure().objectClass(IndexedFieldPerformance.class).objectField("index").indexed(true);
                store();
                query();
            }
        }).start();
    }

    public IndexedFieldPerformance() {
    }

    public IndexedFieldPerformance(String index) {
        this.index = index;
    }

    public static void store() {
        new File(FILE).delete();
        ObjectContainer objectContainer = Db4o.openFile(FILE);
        
        long start = System.currentTimeMillis();
        long elapsed;
        for (int i = 1; i <= SIZE; i++) {
            objectContainer.set(new IndexedFieldPerformance("" + i));
            if (((double) i / (double) COMMIT_STEP) == i / COMMIT_STEP) {
                objectContainer.commit();
                objectContainer.ext().purge();
                elapsed = System.currentTimeMillis() - start; 
                System.out.println("Committed " + i + " from " + SIZE + " elapsed " + elapsed + "ms");
            }
        }
        elapsed = System.currentTimeMillis() - start;
        objectContainer.close();
        System.out.println("Time to store " + SIZE + " objects: " + elapsed + "ms");
    }
    
    public static void query() {
        ObjectContainer objectContainer = Db4o.openFile(FILE);
        long time = System.currentTimeMillis();
        for (int i = 1; i <= QUERIES; i++) {
            Query q = objectContainer.query();
            q.constrain(IndexedFieldPerformance.class);
            q.descend("index").constrain("" + i);
            q.execute();
        }
        time = System.currentTimeMillis() - time;
        objectContainer.close();
        System.out.println("\nTime for  " + QUERIES + " queries against an indexed field in " + SIZE + " objects:\n"  + time + "ms");
        double perQuery = (double)time / (double)1000;
        System.out.println("\nTime per query:\n"  + perQuery + "ms");
        int perSecond = (int)((double)1000 / perQuery);
        System.out.println("\nQueries per second:\n" + perSecond);
    }
}