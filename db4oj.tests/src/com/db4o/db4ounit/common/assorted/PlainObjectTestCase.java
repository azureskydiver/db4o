package com.db4o.db4ounit.common.assorted;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;


public class PlainObjectTestCase extends AbstractDb4oTestCase{

    public static void main(String[] args) {
        new PlainObjectTestCase().runSolo();
    }
    
    public static class Item{
        
        public Object _plainObject;
        
    }
    
    protected void store() throws Exception {
        Item item = new Item();
        item._plainObject = new Object();
        store(item);
    }
    
    public void test(){
        Item item = (Item) retrieveOnlyInstance(Item.class);
        Assert.isNotNull(item._plainObject);
    }

}
