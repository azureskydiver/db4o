package com.db4o.db4ounit.common.assorted;

import com.db4o.YapClient;
import com.db4o.config.Configuration;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.ext.ObjectInfo;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

public class ObjectVersionTest extends AbstractDb4oTestCase {
	
	protected void configure(Configuration config) {
		config.generateUUIDs(Integer.MAX_VALUE);
		config.generateVersionNumbers(Integer.MAX_VALUE);
	}

	public void test() {
		final ExtObjectContainer oc = this.db();
		String msg = oc instanceof YapClient ? "running in C/S" : "Solo mode";
		System.out.println(msg);

		SimplestPossibleItem object = new SimplestPossibleItem("c1");
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

		Assert.isNotNull(objectInfo1.getUUID());
		Assert.isNotNull(objectInfo2.getUUID());

		Assert.isTrue(oldVer > 0);
		Assert.isTrue(newVer > 0);

		Assert.areEqual(objectInfo1.getUUID(), objectInfo2.getUUID());
		Assert.isTrue(newVer > oldVer);
	}

	private void refresh(ExtObjectContainer oc, SimplestPossibleItem object) {
		//TODO Uncomment to pass this test in Client Server
		//is passing in db4o 5.7
		
		//oc.refresh(object, 1);
	}
}
