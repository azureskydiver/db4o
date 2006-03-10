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

	private static ObjectContainer _objectcontainer;

	public static final String PROVIDER_B_FILE = "providerB.yap";

	static MemoryFile memoryFile;

	public static void configure() {
		Test.MEMORY_FILE = true;

		Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
		Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
		new File(PROVIDER_B_FILE).delete();
	}

	public static TestableReplicationProviderInside providerB() {
		if (_objectcontainer == null) {
			if (Test.MEMORY_FILE) {
				memoryFile = new MemoryFile();
				_objectcontainer = ExtDb4o.openMemoryFile(memoryFile);
			} else
				_objectcontainer = Db4o.openFile(PROVIDER_B_FILE);
		}
		return new Db4oReplicationProvider(_objectcontainer, "db4o (b)");
	}

	public static void close() {
		if (_objectcontainer != null) {
			_objectcontainer.close();
			_objectcontainer = null;
		}
	}
}
