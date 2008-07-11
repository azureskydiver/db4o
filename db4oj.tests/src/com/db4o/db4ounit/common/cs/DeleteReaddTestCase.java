/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.ext.*;

import db4ounit.extensions.*;


public class DeleteReaddTestCase extends Db4oClientServerTestCase {
    
    public static class ItemParent {
        
    }
    
    public static class Item extends ItemParent{
        
        public String name;
        
        public Item(String name_){
            name = name_;
        }
    }
    
    protected void store() throws Exception {
        store(new Item("one"));
    }
    
    public void testDeleteReadd(){
        ExtObjectContainer client1 = db();
        ExtObjectContainer client2 = openNewClient();
        
        Item item1 = (Item) retrieveOnlyInstance(client1, Item.class);
        Item item2 = (Item) retrieveOnlyInstance(client2, Item.class);
        
        client1.delete(item1);
        
        client1.commit();
        
        client2.store(item2);
        client2.commit();
        client2.close();
        
        retrieveOnlyInstance(client1, Item.class);
        retrieveOnlyInstance(client1, ItemParent.class);
    }
    
    public static void main(String[] arguments) {
        new DeleteReaddTestCase().runClientServer();
    }

}
