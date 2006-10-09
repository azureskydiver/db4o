/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.ext.Db4oUUID;
import com.db4o.ext.ExtObjectContainer;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class InvalidUUID extends ClientServerTestCase {

	public String name;

	public void configure(Configuration config) {
		config.objectClass(this.getClass()).generateUUIDs(true);
	}

	public void store(ExtObjectContainer oc) {
		name = "theOne";
		oc.set(this);
	}

	public void conc(ExtObjectContainer oc) {
		ObjectSet os = oc.query(InvalidUUID.class);
		if (os.size() == 0) { // already deleted by other threads
			return;
		}
		Assert.areEqual(1, os.size());
		InvalidUUID iu = (InvalidUUID) os.next();
		Db4oUUID myUuid = oc.getObjectInfo(iu).getUUID();
		Assert.isNotNull(myUuid);

		byte[] mySignature = myUuid.getSignaturePart();
		long myLong = myUuid.getLongPart();

		long unknownLong = Long.MAX_VALUE - 100;
		byte[] unknownSignature = new byte[] { 1, 2, 4, 99, 33, 22 };

		Db4oUUID unknownLongPart = new Db4oUUID(unknownLong, mySignature);
		Db4oUUID unknownSignaturePart = new Db4oUUID(myLong, unknownSignature);
		Db4oUUID unknownBoth = new Db4oUUID(unknownLong, unknownSignature);

		Assert.isNull(oc.getByUUID(unknownLongPart));
		Assert.isNull(oc.getByUUID(unknownSignaturePart));
		Assert.isNull(oc.getByUUID(unknownBoth));

		Assert.isNull(oc.getByUUID(unknownLongPart));

		// wait for other threads
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {

		}
		oc.delete(iu);
		oc.commit();
		Assert.isNull(oc.getByUUID(myUuid));
	}

}
