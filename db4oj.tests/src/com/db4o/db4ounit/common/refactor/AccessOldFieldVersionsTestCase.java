package com.db4o.db4ounit.common.refactor;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;

import db4ounit.*;

public class AccessOldFieldVersionsTestCase implements TestLifeCycle {

	private static final Class<?> ORIG_TYPE = Integer.TYPE;
	private static final String FIELD_NAME = "_value";
	private static final int ORIG_VALUE = 42;
	
	private final String DATABASE_PATH = Path4.getTempFileName();

	public void testRetypedField() {
		final Class<?> targetClazz = RetypedFieldData.class;
		renameClass(ReflectPlatform.fullyQualifiedName(targetClazz));
		assertOriginalField(targetClazz);
	}

	private <T> void assertOriginalField(final Class<T> targetClazz) {
		withDatabase(new DatabaseAction() {
			public void runWith(ObjectContainer db) {
				StoredClass storedClass = db.ext().storedClass(targetClazz);
				StoredField storedField = storedClass.storedField(FIELD_NAME, ORIG_TYPE);
				ObjectSet<T> result = db.query(targetClazz);
				Assert.areEqual(1, result.size());
				T obj = result.next();
				Object value = storedField.get(obj);
				Assert.areEqual(ORIG_VALUE, value);
			}
		});
	}

	public void setUp() throws Exception {
		deleteFile();
		withDatabase(new DatabaseAction() {
			public void runWith(ObjectContainer db) {
				db.store(new OriginalData(ORIG_VALUE));
			}
		});
	}

	public void tearDown() throws Exception {
		deleteFile();
	}

	private void renameClass(String targetName) {
		Configuration config = Db4oEmbedded.newConfiguration();
		config.objectClass(OriginalData.class).rename(targetName);
		withDatabase(config, new DatabaseAction() {
			public void runWith(ObjectContainer db) {
				// do nothing
			}
		});
	}

	private void deleteFile() {
		File4.delete(DATABASE_PATH);
	}

	private void withDatabase(DatabaseAction action) {
		withDatabase(Db4oEmbedded.newConfiguration(), action);
	}
	
	private void withDatabase(Configuration config, DatabaseAction action) {
		ObjectContainer db = Db4oEmbedded.openFile(config, DATABASE_PATH);
		try {
			action.runWith(db);
		}
		finally {
			db.close();
		}
	}
	
	private static interface DatabaseAction {
		void runWith(ObjectContainer db);
	}
	
	public static class OriginalData {
		public int _value;
			
		public OriginalData(int value) {
			_value = value;
		}
	}
	
	public static class RetypedFieldData {
		public String _value;
		
		public RetypedFieldData(String value) {
			_value = value;
		}
	}

	public static class RenamedFieldData {
		public int _newValue;
		
		public RenamedFieldData(int newValue) {
			_newValue = newValue;
		}
	}
}
