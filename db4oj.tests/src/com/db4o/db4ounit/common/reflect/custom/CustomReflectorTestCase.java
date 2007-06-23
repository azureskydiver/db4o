package com.db4o.db4ounit.common.reflect.custom;

import com.db4o.foundation.*;
import com.db4o.foundation.io.*;

import db4ounit.*;

/**
 * This test case serves two purposes:
 * 
 * 1) testing the reflector API
 * 2) documenting a common use case for the reflector API which is adapting an external
 * data model to db4o's internal OO based mechanism.
 * 
 */
public class CustomReflectorTestCase implements TestCase, TestLifeCycle {
	
	private static final String CLASS_NAME = "Cat";
	private static final String[] FIELD_NAMES = new String[] { "name", "troubleMakingScore" };
	private static final String[] FIELD_TYPES = new String[] { "string", "int" };
	private static final PersistentEntry[] ENTRIES = {
		new PersistentEntry(CLASS_NAME, "0", new Object[] { "Biro-Biro", new Integer(9) }),
		new PersistentEntry(CLASS_NAME, "1", new Object[] { "Samira", new Integer(4) }),
		new PersistentEntry(CLASS_NAME, "2", new Object[] { "Ivo", new Integer(2) }),
	};
	
	PersistenceContext _context;
	PersistenceProvider _provider;
	
	public void setUp() {
		initializeContext();
		createEntryClass(CLASS_NAME, FIELD_NAMES, FIELD_TYPES);		
		createIndex(CLASS_NAME, FIELD_NAMES[0]);
		insertEntries();
		
		// TODO: uncomment the line below
		// to really test the provider
//		restartProvider();
	}

	public void testSelectAll() {
		
		Collection4 all = new Collection4(selectAll());
		Assert.areEqual(ENTRIES.length, all.size());
		for (int i=0; i<ENTRIES.length; ++i) {
			PersistentEntry expected = ENTRIES[i];
			PersistentEntry actual = entryByUid(all.iterator(), expected.uid);
			if (actual != null) {
				assertEqualEntries(expected, actual);
				all.remove(actual);
			}			
		}
		Assert.isTrue(all.isEmpty(), all.toString());
	}

	private PersistentEntry entryByUid(Iterator4 iterator, Object uid) {
		while (iterator.moveNext()) {
			PersistentEntry e = (PersistentEntry)iterator.current();
			if (uid.equals(e.uid)) {
				return e;
			}
		}
		return null;
	}
	
	public void _testSelectByField() {
		
		PersistentEntry expected = ENTRIES[1];
		
		Iterator4 found = selectByField(FIELD_NAMES[0], expected.fieldValues[0]);
		Assert.isTrue(found.moveNext(), "Expecting entry '" + expected + "'");		
		PersistentEntry actual = (PersistentEntry)found.current();
		
		assertEqualEntries(expected, actual);
	}
	
	private void initializeContext() {
		_context = new PersistenceContext(dataFile());
		initializeProvider();
	}

	private void initializeProvider() {
		_provider = new Db4oPersistenceProvider();
		_provider.initContext(_context);
	}

	private void insertEntries() {
		PersistentEntry entry = new PersistentEntry(CLASS_NAME, null, null);
		for (int i=0; i<ENTRIES.length; ++i) {
			entry.uid = ENTRIES[i].uid;
			entry.fieldValues = ENTRIES[i].fieldValues;
			// reuse entries so the provider cant assume
			// anything about identity
			insert(entry);
		}
	}

	private void assertEqualEntries(PersistentEntry expected, PersistentEntry actual) {
		Assert.areEqual(expected.className, actual.className);
		Assert.areEqual(expected.uid, actual.uid);
		ArrayAssert.areEqual(expected.fieldValues, actual.fieldValues);
	}

	private Iterator4 selectByField(String fieldName, Object fieldValue) {
		return select(new PersistentEntryTemplate(CLASS_NAME, new String[] { fieldName }, new Object[] { fieldValue }));
	}
	
	private Iterator4 selectAll() {
		return select(new PersistentEntryTemplate(CLASS_NAME, new String[0], new Object[0]));
	}

	private Iterator4 select(PersistentEntryTemplate template) {
		return _provider.select(_context, template);
	}

	private void insert(PersistentEntry entry) {
		_provider.insert(_context, entry);
	}

	private void createIndex(String className, String fieldName) {
		_provider.createIndex(_context, className, fieldName);
	}

	private void createEntryClass(String className, String[] fieldNames,
			String[] fieldTypes) {
		_provider.createEntryClass(_context, className, fieldNames, fieldTypes);
	}
	
	public void tearDown() {
		shutdownProvider();
		shutdownContext();
	}

	private void shutdownContext() {
		File4.delete(_context.url());
		_context = null;
	}

	private void shutdownProvider() {
		_provider.closeContext(_context);
		_provider = null;
	}
	
	void restartProvider() {
		shutdownProvider();
		initializeProvider();
	}

	private String dataFile() {
		return Path4.combine(Path4.getTempPath(), "CustomReflector.db4o");
	}
}
