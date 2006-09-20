/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.assorted;

import com.db4o.Db4o;
import com.db4o.ObjectSet;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

public class ReAddCascadedDeleteTestCase extends AbstractDb4oTestCase {
	
	public static void main(String[] args) {
		new ReAddCascadedDeleteTestCase().runSolo();
	}
    
    public static class Item {
        
        public String _name;
        
        public Item _member;
        
        public Item() {            
        }

        public Item(String name) {
            _name = name;
        }

        public Item(String name, Item member) {
            _name = name;
            _member = member;
        }
    }
    
    protected void configure(){
        Db4o.configure().objectClass(Item.class).cascadeOnDelete(true);
    }
    
    protected void store() {
        db().set(new Item("parent", new Item("child")));
    }
    
    public void testDeletingAndReaddingMember() throws Exception{
        deleteParentAndReAddChild();
        
        reopen();
        
        Assert.isNotNull(query("child"));
        Assert.isNull(query("parent"));
    }

	private void deleteParentAndReAddChild() {
		Item i = query("parent");
        db().delete(i);
        db().set(i._member);
        db().commit();
	}
    
    private Item query(String name){
    	ObjectSet objectSet = db().get(new Item(name));
        if (!objectSet.hasNext()) {
        	return null;
        }
        return (Item) objectSet.next();
    }
}
