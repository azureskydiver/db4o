/* Copyright (C) 2009  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections.transparent.list;

import java.util.*;

import com.db4o.activation.*;
import com.db4o.collections.*;
import com.db4o.db4ounit.jre12.collections.*;
import com.db4o.db4ounit.jre12.collections.transparent.*;
import com.db4o.foundation.*;

/* Copyright (C) 2009  db4objects Inc.   http://www.db4o.com */

import db4ounit.*;

/**
 * @sharpen.remove
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ActivatableArrayListTestCase extends ActivatableListTestCaseBase {
	
	private ListSpec<ActivatableArrayList<CollectionElement>> _spec = 
			new ListSpec<ActivatableArrayList<CollectionElement>>(ActivatableArrayList.class,
					new Closure4<ActivatableArrayList<CollectionElement>>() {
		public ActivatableArrayList<CollectionElement> run() {
			return new ActivatableArrayList();
		}
	});
	
	public ActivatableList<CollectionElement> newActivatableList() {
		return _spec.newActivatableList();
	}
	
	private List<CollectionElement> newPlainList(){
		return _spec.newPlainList();
	}
	
	public void testCreation() {
		new ActivatableArrayList<Object>();
		new ActivatableArrayList<Object>(42);
		new ActivatableArrayList<String>((Collection<String>)new ActivatableArrayList<String>());
	}
	
	public void testClone() throws Exception{
		ActivatableArrayList cloned = (ActivatableArrayList) ((ArrayList)singleList()).clone();
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
	
	public void testTrimToSize() throws Exception{
		List<CollectionElement> singleList = singleList();
		((ArrayList)singleList).trimToSize();
		assertAreEqual(newPlainList(), singleList);
	}
	
	public void testEnsureCapacity(){
		List<CollectionElement> singleList = singleList();
		((ArrayList)singleList).ensureCapacity(10);
		assertAreEqual(newPlainList(), singleList);
	}

}
