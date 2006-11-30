/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.generic;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

/*
 * Please start a separated db4o server on port 0x1111 without Atom and Pilot
 * class deployment before running this test. To start the server, please copy
 * NoClassDeployServer.java to a separated project, and run as java application.
 */
public class NoClassDeployClientTestCase {
	public static int COUNT = 10;

	public static void main(String[] args) throws Exception {
		ObjectContainer oc = Db4o.openClient("localhost", 0x1111, "db4o",
				"db4o");
		testWrite(oc);
		testRead(oc);
		oc.close();
	}

	private static void testWrite(ObjectContainer oc) {
		for (int i = 0; i < COUNT; i++) {
			oc.set(new Atom("hello " + i));
		}

		for (int i = 0; i < COUNT; i++) {
			oc.set(new Pilot("Pilot " + i, i));
		}

		oc.commit();
	}

	private static long testRead(ObjectContainer oc) {
		long start = System.currentTimeMillis();
		Query query = oc.query();
		// query.constrain(Pilot.class);
		ObjectSet os = query.execute();
		System.out.println("size = " + os.size());
		while (os.hasNext()) {
			System.out.println(os.next());
		}
		long end = System.currentTimeMillis();
		long elapsed = (end - start);
		System.out.println("elapsed : " + elapsed + "ms");
		return elapsed;
	}
}
