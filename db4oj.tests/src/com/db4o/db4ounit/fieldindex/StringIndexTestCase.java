/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.fieldindex;

import com.db4o.*;
import com.db4o.foundation.Visitor4;
import com.db4o.inside.freespace.*;
import com.db4o.inside.slots.Slot;
import com.db4o.query.Query;

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
    
    public void testCancelRemoval() throws Exception {
    	add("original");    	
    	db().commit();
    	
    	rename("original", "updated");    	
    	assertExists("updated");
    	
    	rename("updated", "original");
    	db().commit();
    	grafittiFreeSpace();
    	reopen();
    	
    	assertExists("original");
    }
    
    public void testCancelRemovalForMultipleTransactions() throws Exception {
    	final Transaction trans1 = newTransaction();
    	final Transaction trans2 = newTransaction();
    	
    	add("original");    	
    	db().commit();
    	
    	rename(trans1, "original", "updated");
    	rename(trans2, "original", "updated");
    	assertExists(trans1, "updated");
    	
    	rename(trans1, "updated", "original");
    	trans1.commit();
    	grafittiFreeSpace();
    	reopen();
    	
    	assertExists("original");
    }
    
    private void grafittiFreeSpace() {
    	final YapRandomAccessFile file = ((YapRandomAccessFile)db());
		final FreespaceManagerRam fm = (FreespaceManagerRam) file.freespaceManager();
    	fm.traverseFreeSlots(new Visitor4() {
			public void visit(Object obj) {
				Slot slot = (Slot) obj;
				file.writeXBytes(slot.getAddress(), slot.getLength());
			}
		});
	}

	public void testDeletingAndReaddingMember() throws Exception{
		add("original");
    	assertExists("original");
        rename("original", "updated");        
        assertExists("updated");
        Assert.isNull(query("original"));
        reopen();        
        assertExists("updated");
        Assert.isNull(query("original"));
    }

	private void assertExists(String itemName) {
		assertExists(trans(), itemName);
	}

	private void add(final String itemName) {
		add(trans(), itemName);
	}
	
	private void add(Transaction transaction, String itemName) {
		stream().set(transaction, new Item(itemName));
	}
	
	private void assertExists(Transaction transaction, String itemName) {
		Assert.isNotNull(query(transaction, itemName));
	}

	private void rename(Transaction transaction, String from, String to) {
		final Item item = query(transaction, from);
		Assert.isNotNull(item);
		item.name(to);
		stream().set(transaction, item);
	}

    private void rename(String from, String to) {
    	rename(trans(), from, to);
	}

	private Item query(String name){
		return query(trans(), name);
	}
	
	private Item query(Transaction transaction, String name) {
    	ObjectSet objectSet = newQuery(transaction, name).execute();
        if (!objectSet.hasNext()) {
        	return null;
        }
        return (Item) objectSet.next();
    }

	private Query newQuery(Transaction transaction, String itemName) {
		final Query query = stream().query(transaction);
		query.constrain(Item.class);
		query.descend("_name").constrain(itemName);
		return query;
	}
}
