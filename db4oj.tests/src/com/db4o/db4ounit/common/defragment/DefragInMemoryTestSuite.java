/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.defragment;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.defragment.*;
import com.db4o.internal.*;
import com.db4o.io.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.fixtures.*;

public class DefragInMemoryTestSuite extends FixtureBasedTestSuite {

	private static class StorageSpec implements Labeled {
		private final String _label;
		private final Storage _storage;

		public StorageSpec(String label, Storage storage) {
			_label = label;
			_storage = storage;
		}
		
		public Storage storage(Storage storage) {
			return _storage == null ? storage : _storage;
		}

		public String label() {
			return _label;
		}
	}
	
	private final static FixtureVariable<StorageSpec> STORAGE_SPEC_FIXTURE = new FixtureVariable<StorageSpec>();
	
	@Override
	public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[] {
				new SimpleFixtureProvider(STORAGE_SPEC_FIXTURE,
						new StorageSpec("memory", null),
						new StorageSpec("file", TestPlatform.newPersistentStorage())
				)
		};
	}

	@Override
	public Class[] testUnits() {
		return new Class[] {
			DefragInMemoryTestUnit.class,	
		};
	}

	public static class DefragInMemoryTestUnit extends TestWithTempFile {

		public static class Item {
			public int _id;
			
			public Item() {				
			}
			
			public Item(int id) {
				_id = id;
			}
		}

		public static class EvenIdItemsPredicate extends Predicate<Item> {
			@Override
			public boolean match(Item item) {
				return (item._id % 2) == 0;
			}
		}

		private static final int NUM_ITEMS = 100;
		protected static final String URI = "database";
	
		private MemoryStorage _memoryStorage;
		
		public void testInMemoryDefragment() throws Exception {
			store();
			defrag();
			Assert.isSmaller(backupLength(), _memoryStorage.bin(URI).length());
			retrieve();
		}

		private long backupLength() {
			Bin backupBin = backupStorage().open(new BinConfiguration(tempFile(), true, 0, true));
			long backupLength = backupBin.length();
			backupBin.close();
			return backupLength;
		}
	
		private DefragmentConfig defragmentConfig(MemoryStorage storage) {
			DefragmentConfig defragConfig = new DefragmentConfig(URI, tempFile(), new TreeIDMapping());
			defragConfig.db4oConfig(config(storage));
			defragConfig.backupStorage(backupStorage());
			return defragConfig;
		}
	
	
		private Storage backupStorage() {
			return STORAGE_SPEC_FIXTURE.value().storage(_memoryStorage);
		}

		private EmbeddedConfiguration config(Storage storage) {
			EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
			config.common().reflectWith(Platform4.reflectorForType(Item.class));
			config.file().storage(storage);
			return config;
		}
		
		private void defrag() throws IOException {
			DefragmentConfig defragConfig = defragmentConfig(_memoryStorage);
			Defragment.defrag(defragConfig);
		}
	
		private void store() {
			ObjectContainer db = Db4oEmbedded.openFile(config(_memoryStorage), URI);
			for(int itemId = 0; itemId < NUM_ITEMS; itemId++) {
				db.store(new Item(itemId));
			}
			db.commit();
			ObjectSet<Item> result = db.query(new EvenIdItemsPredicate());
			while(result.hasNext()) {
				db.delete(result.next());
			}
			db.close();
		}
	
		private void retrieve() {
			ObjectContainer db = Db4oEmbedded.openFile(config(_memoryStorage), URI);
			ObjectSet<Item> result = db.query(Item.class);
			Assert.areEqual(NUM_ITEMS / 2, result.size());
			while(result.hasNext()) {
				Assert.isTrue((result.next()._id % 2) == 1);
			}
			db.close();
		}

		public void setUp() throws Exception {
			_memoryStorage = new MemoryStorage();
		}

		public void tearDown() throws Exception {
			backupStorage().delete(tempFile());
			super.tearDown();
		}
	}

}