/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import db4ounit.*;


public class InterfaceHandlerUpdateTestCase extends HandlerUpdateTestCaseBase{
    
    
    public static interface ItemInterface {
        
    }
    
    public static class ItemContainer {
        
        ItemInterface _item;
        
        ItemInterface[] _items;
        
        Object[] _objects;
        
        Object _object;
        
        public static ItemContainer createNew(){
            ItemContainer itemContainer = new ItemContainer();
            itemContainer._item = new Item();
            itemContainer._items = newItemInterfaceArray();
            itemContainer._objects = newItemInterfaceArray();
            itemContainer._object = newItemInterfaceArray();
            return itemContainer;
        }

        private static ItemInterface[] newItemInterfaceArray() {
            return new ItemInterface[]{ new Item() };
        }
        
    }
    
    public static class Item implements ItemInterface {
        
        
    }
    
    protected Object[] createValues() {
        return new Object[]{
            ItemContainer.createNew()
        };
    }

    protected Object createArrays() {
        return ItemContainer.createNew();
    }

    protected void assertArrays(Object obj) {
        if(db4oMajorVersion() == 4){
            return;
        }
        ItemContainer itemContainer = (ItemContainer) obj;
        assertItemInterfaceArray(itemContainer._items);
        assertItemInterfaceArray(itemContainer._objects);
        assertItemInterfaceArray((Object[]) itemContainer._object);
    }

    protected void assertValues(Object[] values) {
        if(db4oMajorVersion() == 4){
            return;
        }
        ItemContainer itemContainer = (ItemContainer) values[0];
        assertIsItemInstance(itemContainer._item);
    }

    private void assertIsItemInstance(Object item) {
        Assert.isInstanceOf(Item.class, item);
    }

    private void assertItemInterfaceArray(Object[] items) {
        assertIsItemInstance(items[0]);
    }

    protected String typeName() {
        return "interface";
    }
    

}
