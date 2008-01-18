package com.db4o.db4ounit.common.assorted;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;

import db4ounit.*;

public class RepeatDeleteReaddTestCase implements TestCase {

	private static final String FILE_NAME = Path4.getTempPath() + "/readddelete.db4o";

	public static class ItemA {
		public int _id;

		public ItemA(int id) {
			_id = id;
		}
		
		public String toString() {
			return "A" + _id;
		}
	}

	public static class ItemB {
		public String _name;

		public ItemB(String name) {
			_name = name;
		}

		public String toString() {
			return "A" + _name;
		}
	}

	private final static int NUM_ITEMS_PER_CLASS = 10;
	private final static int DELETE_RATIO = 3;
	private int NUM_RUNS = 10;
	
	public void test() throws IOException {
		for (int idx = 0; idx < NUM_RUNS; idx++) {
			assertRun();
		}
	}

	private void assertRun() throws IOException {
		String fileName = FILE_NAME;
		new File(fileName).delete();
		createDatabase(fileName);
		assertCanRead(fileName);
		new File(fileName).delete();
	}

	private void createDatabase(String fileName) {
		ObjectContainer db = Db4o.openFile(config(), fileName);
		Collection4 removed = new Collection4();
		for(int idx = 0; idx < NUM_ITEMS_PER_CLASS; idx++) {
			ItemA itemA = new ItemA(idx);
			ItemB itemB = new ItemB(fillStr('x', idx));
			db.store(itemA);
			db.store(itemB);
			if((idx % DELETE_RATIO) == 0) {
				removed.add(itemA);
				removed.add(itemB);
			}
		}
		db.commit();
		deleteAndReadd(db, removed);
		db.close();
	}

	private void deleteAndReadd(ObjectContainer db, Collection4 removed) {

		Iterator4 removeIter = removed.iterator();
		while(removeIter.moveNext()) {
			Object cur = removeIter.current();
			db.delete(cur);
		}
		db.commit();

		Iterator4 readdIter = removed.iterator();
		while(readdIter.moveNext()) {
			Object cur = readdIter.current();
			db.store(cur);
		}
		db.commit();
	}

	private void assertCanRead(String fileName) {
		ObjectContainer db = Db4o.openFile(config(), fileName);
		assertResults(db);
		db.close();
	}

	private void assertResults(ObjectContainer db) {
		assertResult(db, ItemA.class);
		assertResult(db, ItemB.class);
	}

	private void assertResult(ObjectContainer db, Class clazz) {
		ObjectSet result = db.query(clazz);

		Assert.areEqual(NUM_ITEMS_PER_CLASS, result.size());
		while(result.hasNext()) {
			Assert.isInstanceOf(clazz, result.next());
		}
	}

	private Configuration config() {
		return Db4o.newConfiguration();
	}
	
	private String fillStr(char ch, int len) {
		StringBuffer buf = new StringBuffer();
		for(int idx = 0; idx < len; idx++) {
			buf.append(ch);
		}
		return buf.toString();
	}
}
