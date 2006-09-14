/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.events;

import com.db4o.events.*;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

public class EventRegistryTestCase extends AbstractDb4oTestCase {
	
	public void testForObjectContainerReturnsSameInstance() {
		Assert.areSame(
				EventRegistryFactory.forObjectContainer(db()),
				EventRegistryFactory.forObjectContainer(db()));
	}

	public void testQueryEvents() {

		EventRegistry registry = EventRegistryFactory.forObjectContainer(db());

		EventRecorder recorder = new EventRecorder();
		
		registry.queryStarted().addListener(recorder);
		registry.queryFinished().addListener(recorder);

		Query q = db().query();
		Assert.areEqual(0, recorder.size());
		q.execute();
		Assert.areEqual(2, recorder.size());
		EventRecord e1 = recorder.get(0);
		Assert.areSame(registry.queryStarted(), e1.e);
		Assert.areSame(q, ((QueryEventArgs)e1.args).query());

		EventRecord e2 = recorder.get(1);
		Assert.areSame(registry.queryFinished(), e2.e);
		Assert.areSame(q, ((QueryEventArgs)e2.args).query());

		recorder.clear();

		registry.queryStarted().removeListener(recorder);
		registry.queryFinished().removeListener(recorder);

		db().query().execute();

		Assert.areEqual(0, recorder.size());
	}
}
