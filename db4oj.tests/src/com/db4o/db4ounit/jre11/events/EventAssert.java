/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre11.events;

import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;

import db4ounit.Assert;


public class EventAssert {
	
	public static void assertNoEvents(final EventRecorder eventRecorder) {
		Assert.areEqual(0, eventRecorder.size());
	}

	public static void assertCommitEvent(final EventRecorder eventRecorder,
			final Event4 expectedEvent, final Object[] expectedAdded,
			final Object[] expectedDeleted, final Object[] expectedUpdated) {
		Assert.areEqual(1, eventRecorder.size());		
		Assert.areSame(expectedEvent, eventRecorder.get(0).e);
		CommitEventArgs args = (CommitEventArgs)eventRecorder.get(0).args;
		assertContainsAll(expectedAdded, args.added(), "added");
		assertContainsAll(expectedDeleted, args.deleted(), "deleted");
		assertContainsAll(expectedUpdated, args.updated(), "updated");
	}
	
	private static void assertContainsAll(Object[] expectedItems, ObjectInfoCollection actualItems, String label) {
		for (int i = 0; i < expectedItems.length; i++) {
			assertContains(expectedItems[i], actualItems, label);
		}
		if (expectedItems.length != Iterators.size(actualItems)) {
			Assert.fail(label + ": " + toString(expectedItems) + " != " + toObjectString(actualItems));
		}
	}

	private static String toObjectString(ObjectInfoCollection actualItems) {
		return Iterators.toString(Iterators.map(actualItems.iterator(), new Function4() {
			public Object apply(Object arg) {
				return ((ObjectInfo)arg).getObject();
			}
		}));
	}

	private static String toString(Object[] expectedItems) {
		return Iterators.toString(Iterators.iterate(expectedItems));
	}

	private static void assertContains(Object expectedItem, ObjectInfoCollection items, String label) {
		final Iterator4 iterator = items.iterator();
		while (iterator.moveNext()) {
			ObjectInfo info = (ObjectInfo)iterator.current();
			if (expectedItem == info.getObject()) {
				return;
			}
		}
		Assert.fail("Object '" + expectedItem + "' not found for " + label + ".");
	}


}
