/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.assorted;

import com.db4o.*;

import db4ounit.*;
import db4ounit.db4o.*;


public class ReaddCascadedDelete extends Db4oTestCase {
    
    public static class Item{
        
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
    
    public void configure(){
        Db4o.configure().objectClass(Item.class).cascadeOnDelete(true);
    }
    
    protected void store() {
        db().set(new Item("parent", new Item("child")));
    }
    
    public void testDeletingAndReaddingMember() throws Exception{
        Item i = query("parent");
        db().delete(i);
        db().set(i._member);
        db().commit();
        
        reopen();
        
        Assert.isNotNull(query("child"));
        Assert.isNull(query("parent"));
    }
    
    private Item query(String name){
        ObjectSet objectSet = db().get(new Item(name));
        Assert.areEqual(1, objectSet.size());
        return (Item) objectSet.next();
    }
    

}
