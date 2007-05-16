/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.freespace;

import com.db4o.config.*;
import com.db4o.internal.freespace.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;


public class FreespaceMigrationTestCase extends FreespaceManagerTestCaseBase implements OptOutCS{
    
    private Configuration configuration;

    public static void main(String[] args) {
        new FreespaceMigrationTestCase().runSolo();
    }
    
    protected void configure(Configuration config) {
        super.configure(config);
        config.freespace().useBTreeSystem();
        configuration = config;
    }
    
    public void testSwitchingBackAndForth() throws Exception{
        produceSomeFreeSpace();
        db().commit();
        for (int i = 0; i < 5; i++) {
            int oldFreespace = currentFreespaceManager().totalFreespace();
            configuration.freespace().useRamSystem();
            reopen();
            Assert.isInstanceOf(RamFreespaceManager.class, currentFreespaceManager());
            int newFreespace = currentFreespaceManager().totalFreespace();
            if(i > 2){
                Assert.areEqual(oldFreespace, newFreespace);
            }
            configuration.freespace().useBTreeSystem();
        }

    }

}
