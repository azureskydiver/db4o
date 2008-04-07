/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
package com.db4o.drs.test;

import java.util.List;
import java.util.Map;
import java.util.Iterator;

import com.db4o.Db4o;
import com.db4o.ObjectSet;
import com.db4o.drs.Replication;
import com.db4o.drs.ReplicationEventListener;
import com.db4o.drs.ReplicationSession;
import com.db4o.drs.inside.TestableReplicationProviderInside;

import db4ounit.Assert;
import db4ounit.TestCase;
import db4ounit.TestLifeCycle;

public abstract class DrsTestCase implements TestCase, TestLifeCycle {
	
	public static final Class[] mappings;
	public static final Class[] extraMappingsForCleaning = new Class[]{Map.class, List.class};

	static {
		mappings = new Class[]{
				Replicated.class,
				SPCParent.class, SPCChild.class,
				ListHolder.class, ListContent.class,
				CollectionHolder.class,
				MapContent.class,
				SimpleArrayContent.class, SimpleArrayHolder.class,
				R0.class, Pilot.class, Car.class, Student.class, Person.class};
	}
	

	private final DrsFixturePair _fixtures = DrsFixtureVariable.value();
	
	public void setUp() throws Exception {
		cleanBoth();
		configure();
		openBoth();
		store();
		reopen();
	}

	private void cleanBoth() {
		a().clean();
		b().clean();
	}

	protected void clean() {
		for (int i = 0; i < mappings.length; i++) {
			a().provider().deleteAllInstances(mappings[i]);
			b().provider().deleteAllInstances(mappings[i]);
		}

		for (int i = 0; i < extraMappingsForCleaning.length; i++) {
			a().provider().deleteAllInstances(extraMappingsForCleaning[i]);
			b().provider().deleteAllInstances(extraMappingsForCleaning[i]);
		}

		a().provider().commit();
		b().provider().commit();
	}

	protected void store() {}
	
	protected void configure() {
		Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
		Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
	}
	
	protected void reopen() throws Exception {
		closeBoth();
		openBoth();
	}

	private void openBoth() throws Exception {
		a().open();
		b().open();
	}
	
	public void tearDown() throws Exception {
		closeBoth();
		cleanBoth();
	}

	private void closeBoth() throws Exception {
		a().close();
		b().close();
	}
	
	public DrsFixture a() {
		return _fixtures.a;
	}

	public DrsFixture b() {
		return _fixtures.b;
	}

	protected void ensureOneInstance(DrsFixture fixture, Class clazz) {
		ensureInstanceCount(fixture, clazz, 1);
	}

	protected void ensureInstanceCount(DrsFixture fixture, Class clazz, int count) {
		ObjectSet objectSet = fixture.provider().getStoredObjects(clazz);
		Assert.areEqual(count, objectSet.size());
	}

	protected Object getOneInstance(DrsFixture fixture, Class clazz) {
		Iterator objectSet = fixture.provider().getStoredObjects(clazz).iterator();
		
		Object candidate = null;
		if (objectSet.hasNext()) {
			candidate = objectSet.next();
			
			if (objectSet.hasNext())
				 throw new RuntimeException("Found more than one instance of + " + clazz + " in provider = " + fixture);	 
		}
		
		return candidate;
	}

	protected void replicateAll(TestableReplicationProviderInside providerFrom, TestableReplicationProviderInside providerTo) {
		//System.out.println("from = " + providerFrom + ", to = " + providerTo);
		final ReplicationSession replication = Replication.begin(providerFrom, providerTo);
		final ObjectSet changedSet = providerFrom.objectsChangedSinceLastReplication();
		if (changedSet.size() == 0)
			throw new RuntimeException("Can't find any objects to replicate");
		
		replicateAll(replication, changedSet.iterator());
	}

	private void replicateAll(final ReplicationSession replication,
			final Iterator allObjects) {
		while (allObjects.hasNext()) {
			Object changed = allObjects.next();
			//System.out.println("changed = " + changed);
			replication.replicate(changed);
		}
		replication.commit();
	}
	
	protected void replicateAll(
			TestableReplicationProviderInside from, TestableReplicationProviderInside to, ReplicationEventListener listener) {
		ReplicationSession replication = Replication.begin(from, to, listener);

		replicateAll(replication, from.objectsChangedSinceLastReplication().iterator());
	}

	protected void delete(Class[] classes) {
		for (int i = 0; i < classes.length; i++) {
			a().provider().deleteAllInstances(classes[i]);
			b().provider().deleteAllInstances(classes[i]);
		}
		
		a().provider().commit();
		b().provider().commit(); 
	}

	protected void replicateClass(TestableReplicationProviderInside providerA, TestableReplicationProviderInside providerB, Class clazz) {
		//System.out.println("ReplicationTestcase.replicateClass");
		ReplicationSession replication = Replication.begin(providerA, providerB);
		Iterator allObjects = providerA.objectsChangedSinceLastReplication(clazz).iterator();
		replicateAll(replication, allObjects);
	}

	protected static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e.toString());
		}
	}

}
