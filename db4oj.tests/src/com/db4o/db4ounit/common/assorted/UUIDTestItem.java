/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.query.*;

import db4ounit.*;

/**
 * @exclude
 */
public class UUIDTestItem {
	public String name;

	public UUIDTestItem() {
	}

	public UUIDTestItem(String name) {
		this.name = name;
	}

	public static void assertItemsCanBeRetrievedByUUID(final ExtObjectContainer container, Hashtable4 uuidCache) {
		Query q = container.query();
		q.constrain(UUIDTestItem.class);
		ObjectSet objectSet = q.execute();
		while (objectSet.hasNext()) {
			UUIDTestItem item = (UUIDTestItem) objectSet.next();
			Db4oUUID uuid = container.getObjectInfo(item).getUUID();			
			Assert.isNotNull(uuid);
			Assert.areSame(item, container.getByUUID(uuid));
			final Db4oUUID cached = (Db4oUUID) uuidCache.get(item.name);
			if (cached != null) {
				Assert.areEqual(cached, uuid);
			} else {
				uuidCache.put(item.name, uuid);
			}
		}
	}
}