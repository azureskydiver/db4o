/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import static com.db4o.qlin.QLinSupport.*;

import java.util.*;

import com.db4o.*;
import com.db4o.drs.versant.*;
import com.db4o.drs.versant.metadata.*;

import db4ounit.*;

public class VodCobraTestCase extends VodDatabaseTestCaseBase implements TestLifeCycle {
	
	private VodCobraFacade _cobra;
	
	public void testStore(){
		long expectedObjectLoid = 2;
		ObjectLifecycleEvent objectLifecycleEvent = new ObjectLifecycleEvent(1, 1, expectedObjectLoid, 3, 4);
		long loid = _cobra.store(objectLifecycleEvent);
		Assert.isGreater(0, loid);
		ObjectLifecycleEvent storedObjectLifecycleEvent = _cobra.objectByLoid(loid);
		Assert.areEqual(expectedObjectLoid, storedObjectLifecycleEvent.objectLoid());
	}
	
	public void testQueryForExtent() {
		ObjectLifecycleEvent original = new ObjectLifecycleEvent(1, 1, 2, 3, 4);
		_cobra.store(original);
		
		Collection<ObjectLifecycleEvent> result = _cobra.query(ObjectLifecycleEvent.class);
		
		Assert.areEqual(1, result.size());
		ObjectLifecycleEvent retrieved = result.iterator().next();
		Assert.areEqual(original, retrieved);
	}
	
	public void testCobraQLin(){
		
		long objectLoid = 42;
		
		ObjectLifecycleEvent event = prototype(ObjectLifecycleEvent.class);
		
		ObjectSet<ObjectLifecycleEvent> events = _cobra.from(ObjectLifecycleEvent.class)
			  .where(event.objectLoid())
			  .equal(objectLoid)
			  .limit(1)
			  .select();
		
		Assert.areEqual(0, events.size());
		
		_cobra.store(new ObjectLifecycleEvent(1, 1, objectLoid, 3, 4));
		
		events = _cobra.from(ObjectLifecycleEvent.class)
			  .where(event.objectLoid())
			  .equal(objectLoid)
			  .limit(1)
			  .select();
		Assert.areEqual(1, events.size());
	}
	
	private void ensureSchemaCreated() {
		VodJdo.createInstance(_vod).close();
	}

	public void setUp() throws Exception {
		_cobra = VodCobra.createInstance(_vod);
		ensureSchemaCreated();
	}

	public void tearDown() throws Exception {
		_cobra.close();
	}

}
