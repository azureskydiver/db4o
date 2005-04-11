/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.model.dbtest;

import java.io.File;
import java.util.LinkedList;
import java.util.TreeMap;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.f1.chapter4.Pilot;

public class CreateTestDatabase {
    
    public CreateTestDatabase() {
        for (int i=0; i < 10000; ++i) {
            final Pilot pilot = new Pilot(Integer.toString(i), i);
            someList.add(pilot);
            bigMap.put(new Integer(i), pilot);
        }
        bigArray = someList.toArray();
    }
    
    private LinkedList someList = new LinkedList();
    
    private Object[] bigArray;
    
    private TreeMap bigMap = new TreeMap();

    private int[] numbers = {1, 2, 3, 4, 5, 4, 3, 2, 1, 0};
    
    private int[][] nested = {
            {1, 2, 3, 4, 5},
            {2, 3, 4, 5, 6},
            {3, 4, 5, 6, 7}
    };
    
    private Pilot[] pilots = {
            new Pilot("Vanneveur Bush", 20),
            new Pilot("Ted Nelson", 5),
            new Pilot("Lee Nackman", 15),
            new Pilot("Tim Berners-Lee", 500)
    };
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        new File("test.yap").delete();
        ObjectContainer database = Db4o.openFile("test.yap");
        
        try {
            database.set(new CreateTestDatabase());
            
            for (int i=0; i < 1000000; ++i) {
                database.set(new Pilot(Integer.toString(i), 0));
                if (i % 10000 == 0) database.commit();
            }
            
        } finally {
            database.close();
        }
    }

}
