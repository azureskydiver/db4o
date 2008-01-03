/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.query.*;
import com.db4o.reflect.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class ClassHandlerTestCase extends AbstractDb4oTestCase {

    public static void main(String[] args) {
        new ClassHandlerTestCase().runSolo();
    }
    
    public static class Item{
        
        public String _name;
        
        public Item _child;
        
        public Item(String name, Item child){
            _name = name;
            _child = child;
        }
    }
    
    private ClassMetadata classHandler() {
        ReflectClass claxx = reflector().forClass(Item.class);
        return stream().container().produceClassMetadata(claxx);
    }
    
    public void testReadWrite() {
        MockWriteContext writeContext = new MockWriteContext(db());
        Item expectedItem = new Item("parent", new Item("child", null));
        classHandler().write(writeContext, expectedItem);
        MockReadContext readContext = new MockReadContext(writeContext);
        Item readItem = (Item) classHandler().read(readContext);
        assertAreEqual(expectedItem, readItem);
    }

    public void testStoreObject() throws Exception{
        Item expectedItem = new Item("parent", new Item("child", null));
        db().store(expectedItem);
        db().purge(expectedItem);
        Query q = db().query();
        q.constrain(Item.class);
        q.descend("_name").constrain("parent");
        ObjectSet objectSet = q.execute();
        Item readItem = (Item) objectSet.next();
        Assert.areNotSame(expectedItem, readItem);
        assertAreEqual(expectedItem, readItem);
    }
    
    private void assertAreEqual(Item expectedItem, Item readItem) {
        Assert.areEqual(expectedItem._name, readItem._name);
        Assert.areEqual(expectedItem._child._name, readItem._child._name);
    }

}
