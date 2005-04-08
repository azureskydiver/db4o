/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.model.dbtest;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.f1.chapter4.Pilot;

public class CreateTestDatabase {

    private int[] numbers = {1, 2, 3, 4, 5, 4, 3, 2, 1, 0};
    
    private int[][] nested = {
            {1, 2, 3, 4, 5},
            {2, 3, 4, 5, 6},
            {3, 4, 5, 6, 7}
    };
    
    private Pilot[] pilots = {
            new Pilot("Vannevur Bush", 20),
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
        } finally {
            database.close();
        }
    }

}
