/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.inside.replication.*;
import com.db4o.replication.ConflictResolver;
import com.db4o.replication.ReplicationSession;
import com.db4o.test.Test;
import com.db4o.ObjectSet;

public abstract class ArrayReplicationTest {

	public void testArrayReplication() {
		TestableReplicationProvider _containerA;
		TestableReplicationProvider _containerB;

		_containerA = prepareProviderA();
		_containerB = prepareProviderB();

		ArrayHolder h1 = new ArrayHolder("h1");
		ArrayHolder h2 = new ArrayHolder("h2");

		h1._array = new ArrayHolder[]{h1};
		h2._array = new ArrayHolder[]{h1, h2, null};

		h1._arrayN = new ArrayHolder[][]{{h1}};

		h2._arrayN = new ArrayHolder[][]{{h1, null}, {null, h2}, {null, null}}; //TODO Fix ReflectArray.shape() and test with innermost arrays of varying sizes:  {{h1}, {null, h2}, {null}}

		_containerB.storeNew(h2);
		_containerB.storeNew(h1);

		final ReplicationSession replication = new GenericReplicationSession(_containerA, _containerB, new ConflictResolver() {
			public Object resolveConflict(ReplicationSession session, Object a, Object b) {
				return null;
			}
		});

		replication.replicate(h2); //Traverses to h1.

		replication.commit();

		ObjectSet objects = _containerA.getStoredObjects(ArrayHolder.class);
		check((ArrayHolder) objects.next());
		check((ArrayHolder) objects.next());
	}

	protected abstract TestableReplicationProvider prepareProviderB();

	protected abstract TestableReplicationProvider prepareProviderA();

	private void check(ArrayHolder holder) {
		if (holder._name.equals("h1"))
			checkH1(holder);
		else
			checkH2(holder);
	}

	private void checkH1(ArrayHolder holder) {
		Test.ensure(holder._array[0] == holder);
		Test.ensure(holder._arrayN[0][0] == holder);
	}

	private void checkH2(ArrayHolder holder) {
		Test.ensure(holder._array[0]._name.equals("h1"));
		Test.ensure(holder._array[1] == holder);
		Test.ensure(holder._array[2] == null);

		Test.ensure(holder._arrayN[0][0]._name.equals("h1"));
		Test.ensure(holder._arrayN[1][0] == null);
		Test.ensure(holder._arrayN[1][1] == holder);
		Test.ensure(holder._arrayN[2][0] == null);
	}

}
