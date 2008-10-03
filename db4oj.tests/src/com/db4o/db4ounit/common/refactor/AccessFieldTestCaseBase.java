package com.db4o.db4ounit.common.refactor;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.io.*;

import db4ounit.*;

public abstract class AccessFieldTestCaseBase {

	private final String DATABASE_PATH = Path4.getTempFileName();

	public void setUp() throws Exception {
		deleteFile();
		withDatabase(new DatabaseAction() {
			public void runWith(ObjectContainer db) {
				db.store(newOriginalData());
			}
		});
	}

	public void tearDown() throws Exception {
		deleteFile();
	}

	protected void renameClass(Class origClazz, String targetName) {
		Configuration config = Db4o.newConfiguration();
		config.objectClass(origClazz).rename(targetName);
		withDatabase(config, new DatabaseAction() {
			public void runWith(ObjectContainer db) {
				// do nothing
			}
		});
	}

	protected abstract Object newOriginalData();

	protected <T, F> void assertField(final Class<T> targetClazz, final String fieldName, final Class<F> fieldType,
			final F fieldValue) {
				withDatabase(new DatabaseAction() {
					public void runWith(ObjectContainer db) {
						StoredClass storedClass = db.ext().storedClass(targetClazz);
						StoredField storedField = storedClass.storedField(fieldName, fieldType);
						ObjectSet<T> result = db.query(targetClazz);
						Assert.areEqual(1, result.size());
						T obj = result.next();
						F value = (F)storedField.get(obj);
						Assert.areEqual(fieldValue, value);
					}
				});
			}

	private void deleteFile() {
		File4.delete(DATABASE_PATH);
	}

	private static interface DatabaseAction {
		void runWith(ObjectContainer db);
	}

	private void withDatabase(DatabaseAction action) {
		withDatabase(Db4o.newConfiguration(), action);
	}

	private void withDatabase(Configuration config, DatabaseAction action) {
		ObjectContainer db = Db4o.openFile(config, DATABASE_PATH);
		try {
			action.runWith(db);
		}
		finally {
			db.close();
		}
	}

}