/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import java.util.*;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.VodCobra.*;
import com.db4o.drs.versant.metadata.*;

import db4ounit.*;

public class VodCobraTestCase extends VodDatabaseTestCaseBase implements TestLifeCycle {
	
	private VodCobra _cobra;
	
	public void testStore(){
		long expectedObjectLoid = 2;
		ObjectLifecycleEvent objectLifecycleEvent = new ObjectLifecycleEvent(1, expectedObjectLoid, 3, 4);
		long loid = _cobra.store(objectLifecycleEvent);
		Assert.isGreater(0, loid);
		ObjectLifecycleEvent storedObjectLifecycleEvent = _cobra.objectByLoid(loid);
		Assert.areEqual(expectedObjectLoid, storedObjectLifecycleEvent.objectLoid());
	}
	
	public void testQueryForExtent() {
		ObjectLifecycleEvent original = new ObjectLifecycleEvent(1, 2, 3, 4);
		_cobra.store(original);
		
		Collection<ObjectLifecycleEvent> result = _cobra.query(ObjectLifecycleEvent.class);
		
		Assert.areEqual(1, result.size());
		ObjectLifecycleEvent retrieved = result.iterator().next();
		Assert.areEqual(original, retrieved);
	}
	
	public void testCobraQueries(){
		
		long objectLoid = 42;
		
		CobraQuery query = new CobraQuery(ObjectLifecycleEvent.class);
		query.equals("objectLoid", objectLoid);
		query.orderBy("timestamp", true);
		query.limit(1);
		
		Object[] loids = query.loids(_cobra);
		
		Assert.isNotNull(loids);
		Assert.areEqual(0, loids.length);
		
		_cobra.store(new ObjectLifecycleEvent(1, objectLoid, 3, 4));
		
		loids = query.loids(_cobra);
		Assert.areEqual(1, loids.length);
	}
	
	private void ensureSchemaCreated() {
		new VodJdo(_vod).close();
	}

	public void setUp() throws Exception {
		_cobra = new VodCobra(_vod);
		ensureSchemaCreated();
	}

	public void tearDown() throws Exception {
		_cobra.close();
	}

}
