/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import java.util.*;

import com.db4o.drs.db4o.*;
import com.db4o.drs.inside.*;

import db4ounit.*;
import db4ounit.fixtures.*;


public class SingleTypeCollectionReplicationTest extends FixtureBasedTestSuite {
	
	@Override
	public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[] {
			new SubjectFixtureProvider(new Object[] {
				collection1(),
				collection2(),
			}),
		};
	}

	private Object collection1() {
		return initialize(
				new CollectionHolder(
					new HashMap(),
					new HashSet(),
					new LinkedList()));
	}
	
	private Object collection2() {
		return initialize(
				new CollectionHolder(
					new Hashtable(),
					new HashSet(),
					new LinkedList()));
	}

	private CollectionHolder initialize(CollectionHolder h1) {
		h1.map.put("1", "one");
		h1.set.add("two");
		h1.list.add("three");
		return h1;
	}

	@Override
	public Class[] testUnits() {
		return new Class[] { TestUnit.class };
	}
	
	public static class TestUnit extends DrsTestCase {
	
		public void test() {
			CollectionHolder h1 = subject();
			
			storeNewAndCommit(a().provider(), h1);
			
			replicateAll(a().provider(), b().provider());
			
			Iterator it = b().provider().getStoredObjects(CollectionHolder.class).iterator();
			Assert.isTrue(it.hasNext());
			
			CollectionHolder replica = (CollectionHolder) it.next();
			assertSameClassIfDb4o(h1.map, replica.map);
			Assert.areEqual("one", replica.map.get("1"));
			
			assertSameClassIfDb4o(h1.set, replica.set);
			Assert.isTrue(replica.set.contains("two"));
			
			assertSameClassIfDb4o(h1.list, replica.list);
			Assert.areEqual("three", replica.list.get(0));
		}

		private CollectionHolder subject() {
			return (CollectionHolder) SubjectFixtureProvider.value();
		}

		private void assertSameClassIfDb4o(final Object expectedInstance,
				final Object actualInstance) {
			if (!isDb4oProvider(a())) return;
			if (!isDb4oProvider(b())) return;
			
			Assert.areSame(expectedInstance.getClass(), actualInstance.getClass());
		}
	
		private boolean isDb4oProvider(final DrsFixture fixture) {
			return fixture.provider() instanceof Db4oReplicationProvider;
		}
		
		private void storeNewAndCommit(
				final TestableReplicationProviderInside provider,
				CollectionHolder h1) {
			provider.storeNew(h1);
			provider.activate(h1);
			provider.commit();
		}
	}
}
