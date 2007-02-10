/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import db4ounit.ArrayAssert;

/**
 * Design of this case is copied from
 * com.db4o.db4ounit.common.types.arrays.ByteArrayTestCase.
 */
public class ByteArrayTest extends DrsTestCase {
	static final int ARRAY_LENGTH = 5;

	static byte[] initial = createByteArray();

	static byte[] modInB = new byte[] { 2, 3, 5, 68, 69 };

	static byte[] modInA = new byte[] { 15, 36, 55, 8, 9, 28, 65 };

	public void test() {
		storeInA();
		replicate();
		modifyInB();
		replicate2();
		modifyInA();
		replicate3();
	}

	private void storeInA() {
		IByteArrayHolder byteArrayHolder = new ByteArrayHolder(
				createByteArray());

		a().provider().storeNew(byteArrayHolder);
		a().provider().commit();

		ensureNames(a(), initial);
	}

	private void replicate() {
		replicateAll(a().provider(), b().provider());

		ensureNames(a(), initial);
		ensureNames(b(), initial);
	}

	private void modifyInB() {
		IByteArrayHolder c = getTheObject(b());

		c.setBytes(modInB);
		b().provider().update(c);
		b().provider().commit();

		ensureNames(b(), modInB);
	}

	private void replicate2() {
		replicateAll(b().provider(), a().provider());

		ensureNames(a(), modInB);
		ensureNames(b(), modInB);
	}

	private void modifyInA() {
		IByteArrayHolder c = getTheObject(a());

		c.setBytes(modInA);
		a().provider().update(c);
		a().provider().commit();

		ensureNames(a(), modInA);
	}

	private void replicate3() {
		replicateAll(a().provider(), b().provider());

		ensureNames(a(), modInA);
		ensureNames(b(), modInA);
	}

	private void ensureNames(DrsFixture fixture, byte[] bs) {
		ensureOneInstance(fixture, IByteArrayHolder.class);
		IByteArrayHolder c = getTheObject(fixture);
		ArrayAssert.areEqual(c.getBytes(), bs);
	}

	private IByteArrayHolder getTheObject(DrsFixture fixture) {
		return (IByteArrayHolder) getOneInstance(fixture,
				IByteArrayHolder.class);
	}

	static byte[] createByteArray() {
		byte[] bytes = new byte[ARRAY_LENGTH];
		for (byte i = 0; i < bytes.length; ++i) {
			bytes[i] = i;
		}
		return bytes;
	}
}