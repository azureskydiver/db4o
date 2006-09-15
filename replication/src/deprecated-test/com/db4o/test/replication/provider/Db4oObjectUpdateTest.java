package com.db4o.test.replication.provider;

import com.db4o.Db4o;
import com.db4o.ObjectServer;
import com.db4o.ext.ExtDb4o;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.ext.MemoryFile;
import com.db4o.ext.ObjectInfo;
import com.db4o.test.Test;
import com.db4o.test.replication.SPCChild;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Db4oObjectUpdateTest {
	private static final String SERVER_FILE = "whatever.yap";
	private static final String USER = "user";
	private static final String PASS = "pass";
	private static final int PORT = 9587;

	public static void teast() {
		ExtObjectContainer container = openClientServer();

		SPCChild child = new SPCChild("c1");
		container.set(child);
		container.commit();

		ObjectInfo objectInfo1 = container.getObjectInfo(child);
		long oldVer = objectInfo1.getVersion();

		child.setName("c3");

		container.set(child);
		container.commit();

		ObjectInfo objectInfo2 = container.getObjectInfo(child);
		long newVer = objectInfo2.getVersion();

		Test.ensure(objectInfo1.getUUID() != null);
		Test.ensure(objectInfo2.getUUID() != null);

		Test.ensureEquals(objectInfo1.getUUID(), objectInfo2.getUUID());
		Test.ensure(newVer > oldVer);
	}

	public static void testCollection() {
		ExtObjectContainer oc = openClientServer();
		//ExtObjectContainer oc = openMemory();

		ArrayList list = new ArrayList();
		oc.set(list);
		oc.commit();
		Test.ensure(oc.getObjectInfo(list).getUUID() != null);
	}

	private static ExtObjectContainer openMemory() {
		return ExtDb4o.openMemoryFile(new MemoryFile()).ext();
	}

	private static ExtObjectContainer openClientServer() {
		new File(SERVER_FILE).deleteOnExit();
		ObjectServer server = ExtDb4o.openServer(SERVER_FILE, PORT);
		server.grantAccess(USER, PASS);
		ExtObjectContainer oc;

		try {
			oc = ExtDb4o.openClient("localhost", PORT, USER, PASS).ext();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return oc;
	}

	public static void main(String[] args) {
		Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
		Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
		teast();
		//testCollection();
		System.exit(0);
	}
}
