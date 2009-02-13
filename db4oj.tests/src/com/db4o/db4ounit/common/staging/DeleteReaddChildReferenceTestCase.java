/* Copyright (C) 2009  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.staging;

import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * COR-1539  Readding a deleted object from a different client changes database ID in embedded mode
 */

public class DeleteReaddChildReferenceTestCase extends Db4oClientServerTestCase{
	
	
    public static class ItemParent {
    	
    	public Item child;
        
    }
    
    public static class Item {
        
        public String name;
        
        public Item(String name_){
            name = name_;
        }
    }
    
    
    protected void store() throws Exception {
        Item child = new Item("child");
        ItemParent parent = new ItemParent();
        parent.child = child;
		store(parent);
    }
    
    public void testDeleteReadd(){
        ExtObjectContainer client1 = db();
        ExtObjectContainer client2 = openNewClient();
        
        ItemParent parent1 = retrieveOnlyInstance(client1, ItemParent.class);
        ItemParent parent2 = retrieveOnlyInstance(client2, ItemParent.class);
        
        
        client1.delete(parent1.child);
        
        client1.commit();
        
        client2.store(parent2.child);
        client2.commit();
        client2.close();
        
        ItemParent parent3 = retrieveOnlyInstance(client1, ItemParent.class);
        db().refresh(parent3, Integer.MAX_VALUE);
        Assert.isNotNull(parent3.child);
    }

    public static void main(String[] arguments) {
        new DeleteReaddChildReferenceTestCase().runAll();
    }

}
