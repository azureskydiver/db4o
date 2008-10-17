/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.*;
import com.db4o.ext.*;

import db4ounit.*;


/**
 * @exclude
 */
public class InMemoryObjectContainerTestCase implements TestLifeCycle{
    
    private MemoryFile memoryFile;
    private ObjectContainer objectContainer;
    
    private static int STORED_ITEMS = 1000;

    /**
     * @deprecated using deprecated api
     */
    public void setUp() throws Exception {
        memoryFile = new MemoryFile();
        memoryFile.setIncrementSizeBy(100);
        memoryFile.setInitialSize(100);
        objectContainer = ExtDb4o.openMemoryFile(memoryFile);
    }
    
    public static class Item {
        
    }
    
    public void testSizeIncrement(){
        int lastSize = fileSize();
        for (int i = 0; i < STORED_ITEMS; i++) {
            objectContainer.store(new Item());
            Assert.isSmaller(lastSize + 1000, fileSize());
            lastSize = fileSize();
        }
    }

    private int fileSize() {
        return memoryFile.getBytes().length;
    }

    public void tearDown() throws Exception {
        
    }
    
}
