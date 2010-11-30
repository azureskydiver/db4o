/* Copyright (C) 2010   Versant Inc.   http://www.db4o.com */

package com.db4o.db4ounit.optional;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.collections.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.defragment.*;
import com.db4o.filestats.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.slots.*;

import db4ounit.*;

@decaf.Ignore
//@decaf.Remove(decaf.Platform.JDK11)
public class FileUsageStatsTestCase extends TestWithTempFile {

	private static class Child {
	}
	
	private static class Item {
		public int _id;
		public String _name;
		public int[] _arr;
		public List<Child> _list;
		
		public Item(int id, String name, List<Child> list) {
			_id = id;
			_name = name;
			_arr = new int[]{ id };
			_list = list;
		}
	}
	
	public void testFileStats() throws Exception {
		createDatabase(new ArrayList<Slot>());
		assertFileStats();
		defrag();
		assertFileStats();
	}

	private void assertFileStats() {
		FileUsageStats stats = FileUsageStatsCollector.runStats(tempFile(), true);
		Assert.areEqual(stats.fileSize(), stats.totalUsage());
	}

	private void defrag() throws IOException {
		String backupPath = Path4.getTempFileName();
		DefragmentConfig config = new DefragmentConfig(tempFile(), backupPath);
		config.forceBackupDelete(true);
		Defragment.defrag(config);
		File4.delete(backupPath);
	}

	private void createDatabase(final List<Slot> gaps) {
		File4.delete(tempFile());
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().objectClass(Item.class).objectField("_id").indexed(true);
		config.common().objectClass(Item.class).objectField("_name").indexed(true);
		EmbeddedObjectContainer db = Db4oEmbedded.openFile(config, tempFile());
		List<Child> list = new ArrayList<Child>();
		list.add(new Child());
		Item item = new Item(0, "#0", list);
		db.store(item);
		db.commit();
		db.close();
	}
	
}
