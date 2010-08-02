/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import java.util.*;

import javax.jdo.*;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.metadata.*;

import db4ounit.*;

public class VodCobraTestCase extends VodDatabaseTestCaseBase implements TestLifeCycle {
	
	private VodCobra _cobra;
	
	public void testStore(){
		ensureSchemaCreated();
		long expectedObjectLoid = 2;
		ObjectLifecycleEvent objectLifecycleEvent = new ObjectLifecycleEvent(1, expectedObjectLoid, 3, 4);
		long loid = _cobra.store(objectLifecycleEvent);
		Assert.isGreater(0, loid);
		Assert.areEqual(expectedObjectLoid, _cobra.fieldValue(loid, "objectLoid"));
	}
	
	public void testQueryForExtent() {
		ensureSchemaCreated();
		ObjectLifecycleEvent original = new ObjectLifecycleEvent(1, 2, 3, 4);
		_cobra.store(original);
		
		Collection<ObjectLifecycleEvent> result = _cobra.query(ObjectLifecycleEvent.class);
		
		Assert.areEqual(1, result.size());
		ObjectLifecycleEvent retrieved = result.iterator().next();
		Assert.areEqual(original, retrieved);
	}
	
	private void ensureSchemaCreated() {
		PersistenceManager pm = _vod.createPersistenceManager();
		pm.close();
	}

	public void setUp() throws Exception {
		_cobra = new VodCobra(_vod);
		
	}

	public void tearDown() throws Exception {
		_cobra.close();
	}

}
