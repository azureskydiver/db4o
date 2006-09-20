/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.ObjectSet;
import com.db4o.drs.ReplicationSession;
import com.db4o.drs.inside.GenericReplicationSession;
import com.db4o.test.Test;

public class ArrayReplicationTest extends ReplicationTestCase {

	protected void actualTest() {

		if (!_providerA.supportsMultiDimensionalArrays()) return;
		if (!_providerB.supportsMultiDimensionalArrays()) return;

		ArrayHolder h1 = new ArrayHolder("h1");
		ArrayHolder h2 = new ArrayHolder("h2");

		h1._array = new ArrayHolder[]{h1};
		h2._array = new ArrayHolder[]{h1, h2, null};

		h1._arrayN = new ArrayHolder[][]{{h1}};

		h2._arrayN = new ArrayHolder[][]{{h1, null}, {null, h2}, {null, null}}; //TODO Fix ReflectArray.shape() and test with innermost arrays of varying sizes:  {{h1}, {null, h2}, {null}}

		_providerB.storeNew(h2);
		_providerB.storeNew(h1);

		final ReplicationSession replication = new GenericReplicationSession(_providerA, _providerB);

		replication.replicate(h2); //Traverses to h1.

		replication.commit();

		ObjectSet objects = _providerA.getStoredObjects(ArrayHolder.class);
		check((ArrayHolder) objects.next());
		check((ArrayHolder) objects.next());
	}

	protected void clean() {delete(new Class[]{ArrayHolder.class});}

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

	public void test() {
		super.test();
	}

}
