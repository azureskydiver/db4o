package com.db4o.db4ounit.common.assorted;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;


public class PlainObjectTestCase extends AbstractDb4oTestCase{

    public static void main(String[] args) {
        new PlainObjectTestCase().runSolo();
    }
    
    public static class Item{
        
        public String _name;
        
        public Object _plainObject;
        
        public Item(String name, Object plainObject) {
            _name = name;
            _plainObject = plainObject;
        }
        
    }
    
    protected void store() throws Exception {
        Object plainObject = new Object();
        Item item = new Item("one", plainObject);
        store(item);
        retrieveItem("one");
        Assert.isTrue(db().isStored(item._plainObject));
        store(new Item("two", plainObject));
    }
    
    public void test(){
        Item itemOne = retrieveItem("one");
        Assert.isNotNull(itemOne._plainObject);
        Assert.isTrue(db().isStored(itemOne._plainObject));
        Item itemTwo = retrieveItem("two");
        Assert.areSame(itemOne._plainObject, itemTwo._plainObject);
    }

    private Item retrieveItem(String name) {
        Query query = newQuery(Item.class);
        query.descend("_name").constrain(name);
        ObjectSet objectSet = query.execute();
        Assert.areEqual(1, objectSet.size());
        return (Item) objectSet.next();
    }

}
