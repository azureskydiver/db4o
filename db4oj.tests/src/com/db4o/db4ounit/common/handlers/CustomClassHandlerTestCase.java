/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import com.db4o.config.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


public class CustomClassHandlerTestCase extends AbstractDb4oTestCase implements OptOutCS{
    
    static boolean handlerCalled;
    
    public static void main(String[] arguments) {
        new CustomClassHandlerTestCase().runSolo();
    }
    
    public static class Item {
        
    }
    
    protected void configure(Configuration config) {
        super.configure(config);
        config.objectClass(Item.class).installCustomHandler( new VanillaClassHandler() {
            public boolean canNewInstance() {
                handlerCalled = true;
                return super.canNewInstance();
            }
        });
    }
    
    protected void store(){
        store(new Item());
    }
    
    public void test(){
        handlerCalled = false;
        retrieveOnlyInstance(Item.class);
        Assert.isTrue(handlerCalled);
    }

}
