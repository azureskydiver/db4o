/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.db4o;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ext.ExtDb4o;
import com.db4o.ext.MemoryFile;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.test.Test;

import java.io.File;

public class Db4oReplicationTestUtil {
	static boolean MEMORY_FILE = true;

	static ObjectContainer ocB;

	public static final String PROVIDER_B_FILE = "providerB.yap";

	public static void configure() {
		Test.MEMORY_FILE = MEMORY_FILE;

		Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
		Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);

		clean();

		if (MEMORY_FILE) {
			ocB = ExtDb4o.openMemoryFile(new MemoryFile());
		} else
			ocB = Db4o.openFile(PROVIDER_B_FILE);
	}

	private static void clean() {
		new File(PROVIDER_B_FILE).delete();
	}

	public static TestableReplicationProviderInside newProviderA() {
		return new Db4oReplicationProvider(Test.objectContainer(), "db4o-a");
	}

	public static TestableReplicationProviderInside newProviderB() {
		return new Db4oReplicationProvider(ocB, "db4o-b");
	}

	public static void close() {
		if (null != ocB)
			while (!ocB.close()) {}
		clean();
	}
}
