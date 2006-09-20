/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import db4ounit.Assert;
import com.db4o.ObjectSet;
import com.db4o.drs.ReplicationSession;
import com.db4o.drs.inside.GenericReplicationSession;

public class ArrayReplicationTest extends DrsTestCase {

	public void test() {
	
		if (!a().provider().supportsMultiDimensionalArrays()) return;
		if (!b().provider().supportsMultiDimensionalArrays()) return;

		ArrayHolder h1 = new ArrayHolder("h1");
		ArrayHolder h2 = new ArrayHolder("h2");

		h1._array = new ArrayHolder[]{h1};
		h2._array = new ArrayHolder[]{h1, h2, null};

		h1._arrayN = new ArrayHolder[][]{{h1}};

		h2._arrayN = new ArrayHolder[][]{{h1, null}, {null, h2}, {null, null}}; //TODO Fix ReflectArray.shape() and test with innermost arrays of varying sizes:  {{h1}, {null, h2}, {null}}

		b().provider().storeNew(h2);
		b().provider().storeNew(h1);

		final ReplicationSession replication = new GenericReplicationSession(a().provider(), b().provider());

		replication.replicate(h2); //Traverses to h1.

		replication.commit();

		ObjectSet objects = a().provider().getStoredObjects(ArrayHolder.class);
		check((ArrayHolder) objects.next());
		check((ArrayHolder) objects.next());
	}
	
	private void check(ArrayHolder holder) {
		if (holder._name.equals("h1"))
			checkH1(holder);
		else
			checkH2(holder);
	}

	protected void checkH1(ArrayHolder holder) {
		Assert.areEqual(holder._array[0], holder);
		Assert.areEqual(holder._arrayN[0][0], holder);
	}

	protected void checkH2(ArrayHolder holder) {
		Assert.areEqual(holder._array[0]._name, "h1");
		Assert.areEqual(holder._array[1], holder);
		Assert.areEqual(holder._array[2], null);

		Assert.areEqual(holder._arrayN[0][0]._name, "h1");
		Assert.areEqual(holder._arrayN[1][0], null);
		Assert.areEqual(holder._arrayN[1][1], holder);
		Assert.areEqual(holder._arrayN[2][0], null);
	}

}
