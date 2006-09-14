/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.fieldindex;

import com.db4o.ObjectSet;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

/**
 * @exclude
 */
public class StringIndexTestCase extends AbstractDb4oTestCase {
	
	public static void main(String[] args) {
		new StringIndexTestCase().runSolo();
	}
	
	public static class Item {        
        public String _name;
        
        public Item() {            
        }

        public Item(String name) {
            _name = name;
        }
        
        public void name(String name) {
        	_name = name;
        }
	}
    
    public void configure(){
        indexField(Item.class, "_name");
    }
    
    protected void store() {
        db().set(new Item("original"));
    }
    
    public void testDeletingAndReaddingMember() throws Exception{
    	Assert.isNotNull(query("original"));
        rename("original", "updated");        
        Assert.isNotNull(query("updated"));
        Assert.isNull(query("original"));
        reopen();        
        Assert.isNotNull(query("updated"));
        Assert.isNull(query("original"));
    }

    private void rename(String from, String to) {
		final Item item = query(from);
		item.name(to);
		db().set(item);
	}

	private Item query(String name){
    	ObjectSet objectSet = db().get(new Item(name));
        if (!objectSet.hasNext()) {
        	return null;
        }
        return (Item) objectSet.next();
    }
}
