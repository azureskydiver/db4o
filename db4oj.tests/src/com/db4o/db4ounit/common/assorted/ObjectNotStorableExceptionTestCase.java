/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class ObjectNotStorableExceptionTestCase extends AbstractDb4oTestCase{

    public static void main(String[] args) {
        new ObjectNotStorableExceptionTestCase().runSolo();
    }
    
    public static class Item {
        public Object nakedObject;
    }
    
    public void testObjectContainerAliveAfterObjectNotStorableException(){
        
        final Item item = new Item();
        item.nakedObject = new Object();
        
        Assert.expect(ObjectNotStorableException.class,new CodeBlock() {
            public void run() throws Throwable {
                store(item);
            }
        });
        
        store(new Item());
        
        Assert.isNotNull(retrieveOnlyInstance(Item.class));
    }

}
