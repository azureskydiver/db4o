package com.db4o.db4ounit.jre12.staging;

import java.util.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class HashtableTestCase extends AbstractDb4oTestCase implements OptOutCS {
	public static void main(String[] args) {
		new HashtableTestCase().runSolo();
	}

	protected void store() throws Exception {
		Hashtable hashtable = new Hashtable();
		for (int i = 0; i < 4; ++i) {
			hashtable.put(new Integer(i), "hello" + i);
		}
		store(hashtable);
	}

	public void test() throws Exception {
		Hashtable hashtable = (Hashtable) retrieveOnlyInstance(Hashtable.class);
		long oldSize = db().systemInfo().totalSize();
		store(hashtable);
		db().commit();
		store(hashtable);
		db().commit();
		long newSize = db().systemInfo().totalSize();
		Assert.areEqual(oldSize, newSize);
	}
}
