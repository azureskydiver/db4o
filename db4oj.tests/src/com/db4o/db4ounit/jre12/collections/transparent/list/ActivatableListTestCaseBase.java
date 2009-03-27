/* Copyright (C) 2009  db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections.transparent.list;

import java.util.*;

import com.db4o.collections.*;
import com.db4o.config.*;
import com.db4o.db4ounit.jre12.collections.*;
import com.db4o.db4ounit.jre12.collections.transparent.*;
import com.db4o.ta.*;

import db4ounit.extensions.*;

/**
 * @sharpen.remove
 */
@decaf.Remove(decaf.Platform.JDK11)
public abstract class ActivatableListTestCaseBase extends AbstractDb4oTestCase {

	public ActivatableListTestCaseBase() {
		super();
	}

	@Override
	protected void configure(Configuration config) throws Exception {
		config.add(new TransparentPersistenceSupport());
	}

	@Override
	protected void store() throws Exception {
		List<CollectionElement> list = newActivatableList();
		ListItem<CollectionElement> item = new ListItem<CollectionElement>();
		item._list = list;
		store(item);
	}

	protected void assertAreEqual(List<CollectionElement> elements, List<CollectionElement> singleList) {
		IteratorAssert.areEqual(elements.iterator(), singleList.iterator());
	}

	protected ListItem singleItem() {
		return retrieveOnlyInstance(ListItem.class);
	}

	protected List<CollectionElement> singleList() {
		return singleItem()._list;
	}

	protected abstract ActivatableList<CollectionElement> newActivatableList();
}