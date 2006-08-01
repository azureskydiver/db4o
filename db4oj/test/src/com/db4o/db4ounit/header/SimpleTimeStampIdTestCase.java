/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.header;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.db4o.*;


public class SimpleTimeStampIdTestCase extends Db4oTestCase{
    
    public static class STSItem{
        
        public String _name;
        
        public STSItem() {
            
        }

        public STSItem(String name) {
            _name = name;
        }
        
    }
    
    protected void configure() {
        ObjectClass objectClass = Db4o.configure().objectClass(STSItem.class);
        objectClass.generateUUIDs(true);
        objectClass.generateVersionNumbers(true);
    }
    
    protected void store() {
        db().set(new STSItem("one"));
    }
    
    public void test() throws Exception{
        STSItem item =  (STSItem) db().get(STSItem.class).next();
        
        long version = db().getObjectInfo(item).getVersion();
        Assert.isTrue( version > 0);
        
        Assert.isTrue(((YapFile)db()).currentVersion() >= version);
        
        reopen();
        
        STSItem item2 = new STSItem("two");
        db().set(item2);
        
        long secondVersion = db().getObjectInfo(item2).getVersion();
        
        Assert.isTrue(secondVersion > version);
        Assert.isTrue(((YapFile)db()).currentVersion() >= version);
        
    }
    
    

}
