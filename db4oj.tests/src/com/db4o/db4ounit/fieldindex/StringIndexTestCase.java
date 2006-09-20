/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.fieldindex;

import com.db4o.*;
import com.db4o.db4ounit.btree.ExpectingVisitor;
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
        public String name;
        
        public Item() {            
        }

        public Item(String name_) {
            name = name_;
        }
	}
    
    protected void configure(){
        indexField(Item.class, "name");
    }
    
    public void testNotEquals() {
    	add("foo");
    	add("bar");
    	add("baz");
    	add(null);
    	
    	final Query query = newQuery(Item.class);
    	query.descend("name").constrain("bar").not();
		assertItems(new String[] { "foo", "baz", null }, query.execute());
    }

	private void assertItems(final String[] expected, final ObjectSet result) {
		final ExpectingVisitor expectingVisitor = new ExpectingVisitor(toObjectArray(expected));
    	while (result.hasNext()) {
    		expectingVisitor.visit(((Item)result.next()).name);
    	}
    	expectingVisitor.assertExpectations();
	}
    
    private Object[] toObjectArray(String[] source) {
    	Object[] array = new Object[source.length];
    	System.arraycopy(source, 0, array, 0, source.length);
    	return array;
	}

	public void testCancelRemovalRollback() throws Exception {
    	
    	prepareCancelRemoval(trans(), "original");
    	rename("original", "updated");
    	db().rollback();
    	grafittiFreeSpace();
    	reopen();
    	
    	assertExists("original");
    }
    
    public void testCancelRemovalRollbackForMultipleTransactions() throws Exception {
    	final Transaction trans1 = newTransaction();
    	final Transaction trans2 = newTransaction();
        
        prepareCancelRemoval(trans1, "original");
        assertExists(trans2, "original");
    	
        trans1.rollback();
        assertExists(trans2, "original");
        
        add(trans2, "second");
        assertExists(trans2, "original");
        
        trans2.commit();
        assertExists(trans2, "original");
        
    	grafittiFreeSpace();
        reopen();
        assertExists("original");
    }
    
    public void testCancelRemoval() throws Exception {
    	prepareCancelRemoval(trans(), "original");
    	db().commit();
    	grafittiFreeSpace();
    	reopen();
    	
    	assertExists("original");
    }

	private void prepareCancelRemoval(Transaction transaction, String itemName) {
		add(itemName);    	
    	db().commit();
    	
    	rename(transaction, itemName, "updated");    	
    	assertExists(transaction, "updated");
    	
    	rename(transaction, "updated", itemName);
    	assertExists(transaction, itemName);
	}
    
    public void testCancelRemovalForMultipleTransactions() throws Exception {
    	final Transaction trans1 = newTransaction();
    	final Transaction trans2 = newTransaction();
    	
    	prepareCancelRemoval(trans1, "original");
    	rename(trans2, "original", "updated");    	
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
		item.name = to;
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
		query.descend("name").constrain(itemName);
		return query;
	}
}
