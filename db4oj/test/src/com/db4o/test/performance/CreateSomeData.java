/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.performance;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.io.RandomAccessFileAdapter;

/**
 * @exclude
 */
public class CreateSomeData {
	public static final int DEPTH = 3;

	public static final int COUNT = 10;

	public static int c = 100;

	public static class SomeData {
		public int id;

		public SomeData _parent;

		public SomeData(int id, SomeData parent) {
			this.id = id;
			this._parent = parent;
		}

		public String toString() {
			return " " + id;
		}
	}

	public static void main(String[] args) {
		new File(Util.BENCHFILE).delete();
		new File(Util.DBFILE).delete();
		Db4o.configure().io(
				new RecordingIoAdapter(new RandomAccessFileAdapter(),
						Util.BENCHFILE));
		Db4o.configure().optimizeNativeQueries(true);
		ObjectContainer db = Db4o.openFile(Util.DBFILE);

		long start = System.currentTimeMillis();

		for (int i = 1; i <= COUNT; i++) {
			SomeData obj = new SomeData(i, null);

			for (int j = 0; j < DEPTH; j++) {
				obj = new SomeData(c++, obj);
			}
			db.set(obj);

		}
		db.commit();
		System.err.println("to store " + (COUNT + COUNT * DEPTH)
				+ " objects needed " + (System.currentTimeMillis() - start));
		System.gc();
		start = System.currentTimeMillis();
		ObjectSet result = db.query(SomeData.class);
		while (result.hasNext()) {
			System.out.println(result.next());
		}

		// System.out.println(result.size());
		System.err.println("to query and retrive " + result.size()
				+ " objects needed " + (System.currentTimeMillis() - start));
		db.close();
	}
}
