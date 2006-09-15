package com.db4o.test.drs;

import com.db4o.YapClient;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.ext.ObjectInfo;
import com.db4o.test.Test;
import com.db4o.test.replication.SPCChild;

public class ObjectVersionTest {
	public void test() {
		ExtObjectContainer oc = Test.objectContainer();
		String msg = oc instanceof YapClient ? "running in C/S" : "Solo mode";
		System.out.println(msg);

		SPCChild object = new SPCChild("c1");
		oc.set(object);

		refresh(oc, object);
		ObjectInfo objectInfo1 = oc.getObjectInfo(object);
		long oldVer = objectInfo1.getVersion();

		//Update
		object.setName("c3");
		oc.set(object);

		refresh(oc, object);
		ObjectInfo objectInfo2 = oc.getObjectInfo(object);
		long newVer = objectInfo2.getVersion();

		Test.ensure(objectInfo1.getUUID() != null);
		Test.ensure(objectInfo2.getUUID() != null);

		Test.ensure(oldVer > 0);
		Test.ensure(newVer > 0);

		Test.ensureEquals(objectInfo1.getUUID(), objectInfo2.getUUID());
		Test.ensure(newVer > oldVer);
	}

	private void refresh(ExtObjectContainer oc, SPCChild object) {
		//TODO Uncomment to pass this test in Client Server
		//oc.refresh(object, 1);
	}
}
