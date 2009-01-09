/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.enums;

import db4ounit.*;
import db4ounit.extensions.*;



/**
 */
@decaf.Ignore
public class SimpleEnumTestCase extends AbstractDb4oTestCase {
    
    public static void main(String[] arguments) {
        new SimpleEnumTestCase().runEmbeddedClientServer();
    }
    
    public static final class Item {
        
        public TypeCountEnum a;
        
        public Item(){
        }
        
        public Item(TypeCountEnum a_){
            a = a_;
        }
    }
    
    public void test() throws Exception{
        Item storedItem = new Item(TypeCountEnum.A);
        store(storedItem);
        db().commit();
        reopen();
        Item retrievedItem = (Item) retrieveOnlyInstance(Item.class);
        Assert.areSame(retrievedItem.a, TypeCountEnum.A);
    }

}
