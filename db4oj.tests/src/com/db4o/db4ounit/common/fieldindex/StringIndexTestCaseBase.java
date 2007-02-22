/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.fieldindex;

import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.db4ounit.common.btree.ExpectingVisitor;
import com.db4o.foundation.Visitor4;
import com.db4o.internal.*;
import com.db4o.internal.freespace.FreespaceManagerRam;
import com.db4o.internal.slots.Slot;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

/**
 * @exclude
 */
public abstract class StringIndexTestCaseBase extends AbstractDb4oTestCase {

	public static class Item {        
	    public String name;
	    
	    public Item() {            
	    }
	
	    public Item(String name_) {
	        name = name_;
	    }
	}

	public StringIndexTestCaseBase() {
		super();
	}

	protected void configure(Configuration config) {
	    indexField(config, Item.class, "name");
	}

	protected void assertItems(final String[] expected, final ObjectSet result) {
		final ExpectingVisitor expectingVisitor = new ExpectingVisitor(toObjectArray(expected));
		while (result.hasNext()) {
			expectingVisitor.visit(((Item)result.next()).name);
		}
		expectingVisitor.assertExpectations();
	}

	protected Object[] toObjectArray(String[] source) {
		Object[] array = new Object[source.length];
		System.arraycopy(source, 0, array, 0, source.length);
		return array;
	}

	protected void grafittiFreeSpace() {
		final IoAdaptedObjectContainer file = ((IoAdaptedObjectContainer)db());
		final FreespaceManagerRam fm = (FreespaceManagerRam) file.freespaceManager();
		fm.traverseFreeSlots(new Visitor4() {
			public void visit(Object obj) {
				Slot slot = (Slot) obj;
				file.overwriteDeletedBytes(slot.getAddress(), slot.getLength());
			}
		});
	}

	protected void assertExists(String itemName) {
		assertExists(trans(), itemName);
	}

	protected void add(final String itemName) {
		add(trans(), itemName);
	}

	protected void add(Transaction transaction, String itemName) {
		stream().set(transaction, new Item(itemName));
	}

	protected void assertExists(Transaction transaction, String itemName) {
		Assert.isNotNull(query(transaction, itemName));
	}

	protected void rename(Transaction transaction, String from, String to) {
		final Item item = query(transaction, from);
		Assert.isNotNull(item);
		item.name = to;
		stream().set(transaction, item);
	}

	protected void rename(String from, String to) {
		rename(trans(), from, to);
	}

	protected Item query(String name) {
		return query(trans(), name);
	}

	protected Item query(Transaction transaction, String name) {
		ObjectSet objectSet = newQuery(transaction, name).execute();
	    if (!objectSet.hasNext()) {
	    	return null;
	    }
	    return (Item) objectSet.next();
	}

	protected Query newQuery(Transaction transaction, String itemName) {
		final Query query = stream().query(transaction);
		query.constrain(Item.class);
		query.descend("name").constrain(itemName);
		return query;
	}

}