/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.exceptions;

import java.io.*;

import com.db4o.config.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class TSerializableOnStoreExceptionTestCase extends AbstractDb4oTestCase {

	public static void main(String[] args) {
		new TSerializableOnStoreExceptionTestCase().runAll();
	}

	public static class SerializableItem implements Serializable {
		private void writeObject(java.io.ObjectOutputStream out)
				throws IOException {
			throw new IOException();
		}
	}

	/**
	 * @sharpen.if !CF_1_0 && !CF_2_0
	 */

	protected void configure(Configuration config) {
		config.objectClass(SerializableItem.class).translate(new TSerializable());
	}

	/**
	 * @sharpen.if !CF_1_0 && !CF_2_0
	 */
	public void testOnStoreException() {
		Assert.expect(ReflectException.class, IOException.class,
				new CodeBlock() {
					public void run() throws Throwable {
						db().set(new SerializableItem());
					}
				});
	}
}
