/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre11.assorted;

import com.db4o.config.Configuration;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class ObjectNotStorableExceptionTestCase extends AbstractDb4oTestCase{

    public static void main(String[] args) {
        new ObjectNotStorableExceptionTestCase().runSolo();
    }
    
    protected void configure(Configuration config) throws Exception {
        config.callConstructors(true);
        config.exceptionsOnNotStorable(true);
    }
    
    public static class Item {
        
        public Item(Object obj){
            if(obj == null){
                throw new RuntimeException();
            }
        }
        
        public static Item newItem(){
            return new Item(new Object());
        }
    }
    
    public void testObjectContainerAliveAfterObjectNotStorableException(){
        
        final Item item = Item.newItem();
        
        Assert.expect(ObjectNotStorableException.class,new CodeBlock() {
            public void run() throws Throwable {
                store(item);
            }
        });
        
    }

}
