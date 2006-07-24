package com.db4o.db4ounit;

import java.util.Vector;

import com.db4o.events.*;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.db4o.Db4oTestCase;

public class EventRegistryTestCase extends Db4oTestCase {

	static class QueryEvent {
		public String message;

		public Query query;

		public QueryEvent(String message, Query query) {
			this.message = message;
			this.query = query;
		}
	}

	public void testQueryEvents() {

		EventRegistry registry = EventRegistryFactory.forObjectContainer(db());

		final Vector events = new Vector();

		EventListener4 queryStartedListener = new EventListener4() {
			public void onEvent(Event4 e, Object q) {
				events.addElement(new QueryEvent("started", (Query) q));
			}
		};

		EventListener4 queryFinishedListener = new EventListener4() {
			public void onEvent(Event4 e, Object q) {
				events.addElement(new QueryEvent("finished", (Query) q));
			}
		};

		registry.queryStarted().addListener(queryStartedListener);
		registry.queryFinished().addListener(queryFinishedListener);

		Query q = db().query();
		Assert.areEqual(0, events.size());
		q.execute();
		Assert.areEqual(2, events.size());
		QueryEvent e1 = (QueryEvent) events.get(0);
		Assert.areEqual("started", e1.message);
		Assert.areSame(q, e1.query);

		QueryEvent e2 = (QueryEvent) events.get(1);
		Assert.areEqual("finished", e2.message);
		Assert.areSame(q, e2.query);

		events.clear();

		registry.queryStarted().removeListener(queryStartedListener);
		registry.queryFinished().removeListener(queryFinishedListener);

		db().query().execute();

		Assert.areEqual(0, events.size());
	}

	public void testCallbacksFactory() {

		// Callbacks cb = CallbacksFactory.newInstance(db());
		// cb.addListener(new CallbacksListener() {
		// public void onCallback(CallbackEvent e) {
		// if (e instanceof ObjectCanUpdateEvent) {
		// ObjectCanUpdateEvent ue = (ObjectCanUpdateEvent)e;
		// if (ue.subject() instanceof Foo) {
		// Foo foo = (Foo)ue.subject();
		// if (foo.isFrozen()) {
		// e.cancel();
		// }
		// }
		// }
		// }
		// });
		//		
		// cb.addObjectCanUpdateListener(new ObjectCanUpdateListener() {
		// public void onObjectCanUpdate()
		//			
		// });
		//		
		//		
		// objectCanUpdate, objectCanDelete
		//		
		// delegate void CallbackEventHandler(CallbackEvent e);
		//		
		// interface Callbacks {
		// public event CallbackEventHandler CallbackEvent;
		// }
	}

}
