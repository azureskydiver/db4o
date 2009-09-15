/* Copyright (C) 2009  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections.transparent.list;

import java.util.*;

import com.db4o.activation.*;
import com.db4o.collections.*;
import com.db4o.db4ounit.jre12.collections.transparent.*;

import db4ounit.*;

/**
 * @sharpen.remove
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ActivatableLinkedListTestCase extends ActivatableCollectionTestCaseBase<LinkedList<CollectionElement>> {
	
	private CollectionSpec<LinkedList<CollectionElement>> _spec = 
			new CollectionSpec<LinkedList<CollectionElement>>(
					LinkedList.class,
					CollectionFactories.activatableLinkedListFactory(),
					CollectionFactories.plainLinkedListFactory()
			);
	
	public LinkedList<CollectionElement> newActivatableCollection() {
		return _spec.newActivatableCollection();
	}
	
	private LinkedList<CollectionElement> newPlainList(){
		return _spec.newPlainCollection();
	}
	
	public void testCreation() {
		new ActivatableLinkedList<Object>();
		new ActivatableLinkedList<String>((Collection<String>)new ActivatableLinkedList<String>());
	}
	
	public void testClone() throws Exception{
		ActivatableLinkedList cloned = (ActivatableLinkedList) singleLinkedList().clone();
		// assert that activator is null - should throw IllegalStateException if it isn't
		cloned.bind(new Activator() {
			public void activate(ActivationPurpose purpose) {
			}
		});
		IteratorAssert.areEqual(newPlainList().iterator(), cloned.iterator());
	}

	public void testToString(){
		Assert.areEqual(newPlainList().toString(), singleCollection().toString());
	}
	
	public void testAddFirst() throws Exception{
		Element element = new Element("first");
		singleLinkedList().addFirst(element);
		reopen();
		Assert.isTrue(singleLinkedList().contains(element));
	}
	
	public void testAddLast() throws Exception{
		Element element = new Element("last");
		singleLinkedList().addLast(element);
		reopen();
		Assert.isTrue(singleLinkedList().contains(element));
	}
	
	private LinkedList singleLinkedList(){
		return (ActivatableLinkedList) singleCollection();
	}

}
