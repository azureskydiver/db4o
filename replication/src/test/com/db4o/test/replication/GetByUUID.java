/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.Db4o;
import com.db4o.ObjectSet;
import com.db4o.ext.Db4oUUID;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;
import com.db4o.test.Test;

import java.util.Hashtable;

public class GetByUUID {

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
		Test.store(new GetByUUID("one"));
		Test.store(new GetByUUID("two"));
	}

	public void test() {
		Hashtable ht = new Hashtable();
		ExtObjectContainer oc = Test.objectContainer();
		Query q = Test.query();
		q.constrain(GetByUUID.class);
		ObjectSet objectSet = q.execute();
		while (objectSet.hasNext()) {
			GetByUUID gbu = (GetByUUID) objectSet.next();
			Db4oUUID uuid = oc.getObjectInfo(gbu).getUUID();
			GetByUUID gbu2 = (GetByUUID) oc.getByUUID(uuid);
			Test.ensure(gbu == gbu2);
			ht.put(gbu.name, uuid);
		}
		Test.reOpen();
		oc = Test.objectContainer();
		q = Test.query();
		q.constrain(GetByUUID.class);
		objectSet = q.execute();
		while (objectSet.hasNext()) {
			GetByUUID gbu = (GetByUUID) objectSet.next();
			Db4oUUID uuid = (Db4oUUID) ht.get(gbu.name);
			GetByUUID gbu2 = (GetByUUID) oc.getByUUID(uuid);
			Test.ensure(gbu == gbu2);

			oc.delete(gbu);
		}

		oc.close();
	}
}
