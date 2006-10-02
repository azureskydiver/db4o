/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.AbstractFileBasedDb4oFixture;

import java.io.File;


public class SystemInfoTestCase extends AbstractDb4oTestCase{
    
    public static class Item {
        
    }
    
    public static void main(String[] arguments) {
        new SystemInfoTestCase().runSolo();
    }

    public void tearDown() throws Exception {
        super.tearDown();
        Db4o.configure().freespace().useRamSystem();
    }
    
    public void testDefaultFreespaceInfo(){
        assertFreespaceInfo(db().systemInfo());
    }
    
    public void testIndexBasedFreespaceInfo() throws Exception{
        Db4o.configure().freespace().useIndexSystem();
        reopen();
        assertFreespaceInfo(db().systemInfo());
    }
    
    private void assertFreespaceInfo(SystemInfo info){
        Assert.isNotNull(info);
        Item item = new Item();
        db().set(item);
        db().commit();
        db().delete(item);
        db().commit();
        Assert.isTrue(info.freespaceEntryCount() > 0);
        Assert.isTrue(info.freespaceSize() > 0);
    }

    public void testTotalSize(){
        if(fixture() instanceof AbstractFileBasedDb4oFixture ){
        	// assuming YapFile only
            AbstractFileBasedDb4oFixture fixture = (AbstractFileBasedDb4oFixture) fixture();
            File f = new File(fixture.getAbsolutePath());
            long expectedSize = f.length();
            long actual = db().systemInfo().totalSize();
            Assert.areEqual(expectedSize, actual);
        }
    }

}
