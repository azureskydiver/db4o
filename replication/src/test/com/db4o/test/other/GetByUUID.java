/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.other;

import com.db4o.Db4o;
import com.db4o.ObjectSet;
import com.db4o.ext.Db4oUUID;
import com.db4o.query.Query;
import com.db4o.test.replication.db4ounit.DrsTestCase;

import db4ounit.Assert;
import db4ounit.db4o.Db4oTestCase;

import java.util.Hashtable;

public class GetByUUID extends DrsTestCase {

	String name;

	public GetByUUID() {
	}

	public GetByUUID(String name) {
		this.name = name;
	}

	public void configure() {
		Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
	}

	public void store() {
		a().db().set(new GetByUUID("one"));
		a().db().set(new GetByUUID("two"));
	}

	public void test() throws Exception {
		Hashtable ht = new Hashtable();
		//ExtObjectContainer oc = Test.objectContainer();
		Query q = a().db().query();
		q.constrain(GetByUUID.class);
		ObjectSet objectSet = q.execute();
		while (objectSet.hasNext()) {
			GetByUUID gbu = (GetByUUID) objectSet.next();
			Db4oUUID uuid = a().db().getObjectInfo(gbu).getUUID();
			GetByUUID gbu2 = (GetByUUID) a().db().getByUUID(uuid);
			Assert.areEqual(gbu, gbu2);
			ht.put(gbu.name, uuid);
		}
		reopen();
		//oc = Test.objectContainer();
		q = a().db().query();
		q.constrain(GetByUUID.class);
		objectSet = q.execute();
		while (objectSet.hasNext()) {
			GetByUUID gbu = (GetByUUID) objectSet.next();
			Db4oUUID uuid = (Db4oUUID) ht.get(gbu.name);
			GetByUUID gbu2 = (GetByUUID) a().db().getByUUID(uuid);
			Assert.areEqual(gbu, gbu2);

			a().db().delete(gbu);
		}
	}
}
