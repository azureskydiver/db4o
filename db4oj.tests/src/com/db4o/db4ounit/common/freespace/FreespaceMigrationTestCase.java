/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.freespace;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.internal.freespace.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;


public class FreespaceMigrationTestCase extends FreespaceManagerTestCaseBase implements OptOutCS, OptOutDefragSolo {
    
    private Configuration configuration;

    public static void main(String[] args) {
        new FreespaceMigrationTestCase().runSolo();
    }
    
    protected void configure(Configuration config) throws Exception {
        super.configure(config);
        config.freespace().useBTreeSystem();
        configuration = config;
    }
    
    public void testSwitchingBackAndForth() throws Exception{
        produceSomeFreeSpace();
        db().commit();
        int maximumFreespace = stabilizeFreespaceManagerAlterationEffects();
        for (int i = 0; i < 10; i++) {
            
            assertFreespaceSmallerThan(maximumFreespace);
            configuration.freespace().useRamSystem();
            reopen();
            assertFreespaceManagerClass(RamFreespaceManager.class);
            
            assertFreespaceSmallerThan(maximumFreespace);
            configuration.freespace().useBTreeSystem();
            reopen();
            assertFreespaceManagerClass(BTreeFreespaceManager.class);
        }

    }

    private void assertFreespaceManagerClass(Class clazz) {
        Assert.isInstanceOf(clazz, currentFreespaceManager());
    }

    private void assertFreespaceSmallerThan(int maximumFreespace) {
        Assert.isSmaller(maximumFreespace, currentFreespace());
    }

    private int currentFreespace() {
        return currentFreespaceManager().totalFreespace();
    }

    private int stabilizeFreespaceManagerAlterationEffects() throws Exception {
        int maximumFreespace = 0;
        for (int i = 0; i < 3; i++) {
            configuration.freespace().useRamSystem();
            reopen();
            maximumFreespace = Math.max(maximumFreespace, currentFreespace());
            configuration.freespace().useBTreeSystem();
            reopen();
            maximumFreespace = Math.max(maximumFreespace, currentFreespace());
        }
        return maximumFreespace;
    }

}
