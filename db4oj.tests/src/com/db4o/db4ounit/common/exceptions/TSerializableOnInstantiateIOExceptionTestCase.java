/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.exceptions;

import java.io.*;

import com.db4o.config.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @sharpen.ignore
 */

public class TSerializableOnInstantiateIOExceptionTestCase extends
		AbstractDb4oTestCase {

	public static void main(String[] args) {
		new TSerializableOnInstantiateIOExceptionTestCase().runAll();
	}

	public static class SerializableItem implements Serializable {

		private void readObject(java.io.ObjectInputStream in)
				throws IOException, ClassNotFoundException {
			throw new IOException();
		}
	}

	protected void configure(Configuration config) {
		config.objectClass(SerializableItem.class).translate(
				new TSerializable());
	}

	protected void store() throws Exception {
		store(new SerializableItem());
	}

	public void testOnInstantiateException() {
		Assert.expect(ReflectException.class, IOException.class,
				new CodeBlock() {
					public void run() throws Throwable {
						TSerializableOnInstantiateIOExceptionTestCase.this
								.retrieveOnlyInstance(SerializableItem.class);
					}
				});
	}
}
