/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import java.io.Serializable;

import com.db4o.ObjectSet;
import com.db4o.config.TSerializable;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

interface IByteArrayHolder {
	byte[] getBytes();
}

class ByteArrayHolder implements IByteArrayHolder {

	byte[] _bytes;

	public ByteArrayHolder(byte[] bytes) {
		this._bytes = bytes;
	}

	public byte[] getBytes() {
		return _bytes;
	}
}

class SerializableByteArrayHolder implements Serializable, IByteArrayHolder {

	private static final long serialVersionUID = 1L;

	byte[] _bytes;

	public SerializableByteArrayHolder(byte[] bytes) {
		this._bytes = bytes;
	}

	public byte[] getBytes() {
		return _bytes;
	}
}

public class ByteArray extends ClientServerTestCase {

	static final int ITERATIONS = 15;

	static final int INSTANCES = 2;

	static final int ARRAY_LENGTH = 1024 * 512;

	public void store(ExtObjectContainer oc) {
		com.db4o.Db4o.configure()
				.objectClass(SerializableByteArrayHolder.class).translate(
						new TSerializable());
		ExtObjectContainer oc2 = fixture().db();
		try {
			for (int i = 0; i < INSTANCES; ++i) {
				oc2.set(new ByteArrayHolder(createByteArray()));
				oc2.set(new SerializableByteArrayHolder(createByteArray()));
			}
		} finally {
			oc2.close();
		}
	}

	public void concByteArrayHolder(ExtObjectContainer oc) {
		timeQueryLoop(oc, "raw byte array", ByteArrayHolder.class);
	}

	public void concSerializableByteArrayHolder(ExtObjectContainer oc) {
		timeQueryLoop(oc, "TSerializable", SerializableByteArrayHolder.class);
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
