/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.defragment;

import java.io.*;

import com.db4o.*;
import com.db4o.db4ounit.common.migration.*;
import com.db4o.defragment.*;
import com.db4o.foundation.io.*;
import com.db4o.query.*;

import db4ounit.*;

/**
 * test case for COR-785
 */
public class LegacyDatabaseDefragTestCase implements TestCase {
	
	private static final int ITEM_COUNT = 50;

	public static final class Item {
		
		public int value;
		
		public Item() {
		}
		
		public Item(int value) {
			this.value = value;
		}
	}
	
	public void _test() throws Exception {
		final String dbFile = getTempFile();
		createLegacyDatabase(dbFile);
		defrag(dbFile);
		assertContents(dbFile);
	}
	
	private void assertContents(String dbFile) {
		final ObjectContainer container = Db4o.openFile(dbFile);
		try {
			final ObjectSet found = queryItems(container);
			for (int i = 1; i < ITEM_COUNT; i += 2) {
				Assert.isTrue(found.hasNext());
				Assert.areEqual(i, ((Item)found.next()).value);
			}
		} finally {
			container.close();
		}
	}

	private ObjectSet queryItems(final ObjectContainer container) {
		final Query q = container.query();
		q.constrain(Item.class);
		q.descend("value").orderAscending();
		final ObjectSet found = q.execute();
		return found;
	}

	public void createDatabase(String fname) {	
		final ObjectContainer container = Db4o.openFile(fname);
		try {
			fragmentDatabase(container);
		} finally {
			container.close();
		}
	}

	private void fragmentDatabase(final ObjectContainer container) {
		Item[] items = createItems();
		for (int i=0; i<items.length; ++i) {
			container.set(items[i]);
		}
		for (int i=0; i<items.length; i += 2) {
			container.delete(items[i]);
		}
	}

	private Item[] createItems() {
		Item[] items = new Item[LegacyDatabaseDefragTestCase.ITEM_COUNT];
		for (int i=0; i<items.length; ++i) {
			items[i] = new Item(i);
		}
		return items;
	}

	private String getTempFile() throws IOException {
		return Path4.getTempFileName();
	}

	private void defrag(String dbFile) throws IOException {
		final DefragmentConfig config = new DefragmentConfig(dbFile);
		config.upgradeFile(dbFile + ".upgraded");
		Defragment.defrag(config);
	}
	
	private void createLegacyDatabase(String dbFile) throws Exception {		
		Db4oLibrary library = librarian().forVersion("6.1");
		library.environment.invokeInstanceMethod(getClass(), "createDatabase", new Object[] { dbFile });
	}

	private Db4oLibrarian librarian() {
		return new Db4oLibrarian(new Db4oLibraryEnvironmentProvider(PathProvider.testCasePath()));
	}

}
