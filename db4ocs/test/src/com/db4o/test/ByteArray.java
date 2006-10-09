/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import com.db4o.ObjectSet;
import com.db4o.config.TSerializable;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;
import com.db4o.test.persistent.ByteArrayHolder;
import com.db4o.test.persistent.IByteArrayHolder;
import com.db4o.test.persistent.SerializableByteArrayHolder;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class ByteArray extends ClientServerTestCase {

	static final int ITERATIONS = 15;

	static final int INSTANCES = 2;

	static final int ARRAY_LENGTH = 1024 * 512;

	public void store(ExtObjectContainer oc) {
		oc.configure().objectClass(SerializableByteArrayHolder.class)
				.translate(new TSerializable());
		for (int i = 0; i < INSTANCES; ++i) {
			oc.set(new ByteArrayHolder(createByteArray()));
			oc.set(new SerializableByteArrayHolder(createByteArray()));
		}
	}

	public void concByteArrayHolder(ExtObjectContainer oc) {
		timeQueryLoop(oc, "raw byte array", ByteArrayHolder.class);
	}

	public void concByteArrayHolderIndexed1(ExtObjectContainer oc) {
		oc.configure().objectClass(ByteArrayHolder.class).objectField("_bytes")
				.indexed(true);
		oc.configure().objectClass(SerializableByteArrayHolder.class)
				.objectField("_bytes").indexed(true);
		concByteArrayHolder(oc);
	}

	public void concByteArrayHolderIndexed2(ExtObjectContainer oc) {
		oc.configure().objectClass(ByteArrayHolder.class).objectField("_bytes")
				.indexed(true);
		oc.configure().objectClass(SerializableByteArrayHolder.class)
				.objectField("_bytes").indexed(false);
		concByteArrayHolder(oc);
	}

	public void concByteArrayHolderIndexed3(ExtObjectContainer oc) {
		oc.configure().objectClass(ByteArrayHolder.class).objectField("_bytes")
				.indexed(false);
		oc.configure().objectClass(SerializableByteArrayHolder.class)
				.objectField("_bytes").indexed(true);
		concByteArrayHolder(oc);
	}

	public void concSerializableByteArrayHolder(ExtObjectContainer oc) {
		timeQueryLoop(oc, "TSerializable", SerializableByteArrayHolder.class);
	}

	public void concSerializableByteArrayHolderIndexed1(ExtObjectContainer oc) {
		oc.configure().objectClass(ByteArrayHolder.class).objectField("_bytes")
				.indexed(true);
		oc.configure().objectClass(SerializableByteArrayHolder.class)
				.objectField("_bytes").indexed(true);
		concSerializableByteArrayHolder(oc);
	}

	public void concSerializableByteArrayHolderIndexed2(ExtObjectContainer oc) {
		oc.configure().objectClass(ByteArrayHolder.class).objectField("_bytes")
				.indexed(true);
		oc.configure().objectClass(SerializableByteArrayHolder.class)
				.objectField("_bytes").indexed(false);
		concSerializableByteArrayHolder(oc);
	}

	public void concSerializableByteArrayHolderIndexed3(ExtObjectContainer oc) {
		oc.configure().objectClass(ByteArrayHolder.class).objectField("_bytes")
				.indexed(false);
		oc.configure().objectClass(SerializableByteArrayHolder.class)
				.objectField("_bytes").indexed(true);
		concSerializableByteArrayHolder(oc);
	}

	private void timeQueryLoop(ExtObjectContainer oc, String label,
			final Class clazz) {
		for (int i = 0; i < ITERATIONS; ++i) {
			Query query = oc.query();
			query.constrain(clazz);

			ObjectSet os = query.execute();
			Assert.areEqual(INSTANCES, os.size());

			while (os.hasNext()) {
				Assert.areEqual(ARRAY_LENGTH, ((IByteArrayHolder) os.next())
						.getBytes().length);
			}
		}
	}

	byte[] createByteArray() {
		byte[] bytes = new byte[ARRAY_LENGTH];
		for (int i = 0; i < bytes.length; ++i) {
			bytes[i] = (byte) (i % 256);
		}
		return bytes;
	}
}
