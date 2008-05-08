/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.staging;

import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class IntegerWrapperNullInArrayTestCase extends AbstractDb4oTestCase{

    public static void main(String[] args) {
        new IntegerWrapperNullInArrayTestCase().runSolo();
    }
    
    public class Item {
        
        public Integer[] array;
        
    }
    
    private static Integer[] DATA = new Integer[]{ new Integer(1), null, new Integer(2) };
    
    protected void store() throws Exception {
        Item item = new Item();
        item.array = DATA;
        store(item);
    }
    
    public void test(){
        Item item = (Item) retrieveOnlyInstance(Item.class);
        if(NullableArrayHandling.enabled()){
            ArrayAssert.areEqual(DATA, item.array);
        }
    }

}
