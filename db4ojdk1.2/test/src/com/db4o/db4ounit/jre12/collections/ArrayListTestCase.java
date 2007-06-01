/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import com.db4o.collections.facades.*;
import com.db4o.config.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class ArrayListTestCase extends AbstractDb4oTestCase{
    
    public static void main(String[] arguments) {
        new ArrayListTestCase().runSolo();
    }
    
    public static class Item {
        
        public ArrayList arrayList;
        
        public Item() {
        }
        
    }
    
    protected void configure(Configuration config) {
        super.configure(config);
        config.objectClass(ArrayList.class).installCustomHandler(new VanillaClassHandler(){
            public boolean canNewInstance() {
                return true;
            }
            
            public Object newInstance() {
                return new ArrayListFacade();
            }
        });
    }
    
    public void testReplacement() throws Exception{
        Item item = new Item();
        ArrayList arrayList = new ArrayList();
        item.arrayList = arrayList;
        arrayList.add("One");
        store(item);
        
        reopen();
        
        Item retrievedItem = (Item) retrieveOnlyInstance(Item.class);
        ArrayList retrievedArrayList = retrievedItem.arrayList;
        Assert.isInstanceOf(ArrayListFacade.class, retrievedArrayList);
        
        String str = (String) retrievedArrayList.get(0);
        Assert.areEqual("One", str);
    }

}
