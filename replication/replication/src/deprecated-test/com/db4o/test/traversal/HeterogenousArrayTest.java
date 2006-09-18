package com.db4o.test.traversal;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.ext.ExtDb4o;
import com.db4o.ext.MemoryFile;

/**
 * @author patrick
 */
public class HeterogenousArrayTest {
	private static class WithArray {
		int[][] data;
	}

	public static void main(String[] args) {
		ObjectContainer db = ExtDb4o.openMemoryFile(new MemoryFile());
		WithArray data = new WithArray();
		data.data = new int[][]{{1, 2, 3}, {4, 5}};
		db.set(data);
		db.commit();
		data = null;
		System.gc();
		ObjectSet result = db.get(WithArray.class);
		data = (WithArray) result.next();
		System.out.println(data.data[1].length);
		System.exit(0);
	}
}
