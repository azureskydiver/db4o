/* Copyright (C) 2009  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections.transparent.list;

import java.util.*;

import com.db4o.activation.*;
import com.db4o.collections.*;
import com.db4o.db4ounit.jre12.collections.*;
import com.db4o.db4ounit.jre12.collections.transparent.*;
import com.db4o.foundation.*;

import db4ounit.*;

/**
 * @sharpen.remove
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ActivatableLinkedListTestCase extends ActivatableListTestCaseBase {
	
	private ListSpec<ActivatableLinkedList<CollectionElement>> _spec = 
			new ListSpec<ActivatableLinkedList<CollectionElement>>(ActivatableLinkedList.class,
					new Closure4<ActivatableLinkedList<CollectionElement>>() {
		public ActivatableLinkedList<CollectionElement> run() {
			return new ActivatableLinkedList();
		}
	});
	
	public ActivatableList<CollectionElement> newActivatableList() {
		return _spec.newActivatableList();
	}
	
	private List<CollectionElement> newPlainList(){
		return _spec.newPlainList();
	}
	
	public void testCreation() {
		new ActivatableLinkedList<Object>();
		new ActivatableLinkedList<String>((Collection<String>)new ActivatableLinkedList<String>());
	}
	
	public void testClone() throws Exception{
		ActivatableLinkedList cloned = (ActivatableLinkedList) ((LinkedList)singleList()).clone();
		// assert that activator is null - should throw IllegalStateException if it isn't
		cloned.bind(new Activator() {
			public void activate(ActivationPurpose purpose) {
			}
		});
		IteratorAssert.areEqual(newPlainList().iterator(), cloned.iterator());
	}

	public void testToString(){
		Assert.areEqual(newPlainList().toString(), singleList().toString());
	}

}
