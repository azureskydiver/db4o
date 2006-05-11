package com.db4o.test.other;

import com.db4o.ext.ExtObjectContainer;
import com.db4o.ext.ObjectInfo;
import com.db4o.test.Test;
import com.db4o.test.replication.SPCChild;
import com.db4o.YapClient;

import java.util.ArrayList;

public class CollectionUuidTest {
	public void test() {
		ExtObjectContainer oc = Test.objectContainer();

		ArrayList list = new ArrayList();
		oc.set(list);
		oc.commit();
		Test.ensure(oc.getObjectInfo(list).getUUID() != null);
	}
}
