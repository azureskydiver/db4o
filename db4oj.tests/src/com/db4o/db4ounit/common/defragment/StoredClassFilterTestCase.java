package com.db4o.db4ounit.common.defragment;

import java.io.*;

import com.db4o.*;
import com.db4o.defragment.*;
import com.db4o.ext.*;
import com.db4o.foundation.io.*;
import com.db4o.reflect.*;

import db4ounit.*;


public class StoredClassFilterTestCase implements TestCase {

	private static final String DB4O_BACKUP = buildTempPath("defrag.db4o.backup");
	private static final String DB4O_FILE = buildTempPath("defrag.db4o");

	public static class SimpleClass {
		public String _simpleField;
		public SimpleClass(String simple){
			_simpleField = simple;
		}
	}
	
	public static void main(String[] args) {
		new TestRunner(StoredClassFilterTestCase.class).run();
	}
	
	private static String buildTempPath(String fname) {
		return com.db4o.db4ounit.util.IOServices.buildTempPath(fname);
	}

	public void test() throws Exception {
		deleteAllFiles();
		String fname = createDatabase();
		defrag(fname);
		assertStoredClasses(fname);
	}

	private void deleteAllFiles() {
		File4.delete(DB4O_FILE);
		File4.delete(DB4O_BACKUP);		
	}

	private void assertStoredClasses(String fname) {
		ObjectContainer db = Db4o.openFile(fname);
		try {
			ReflectClass[] knownClasses = db.ext().knownClasses();
			assertKnownClasses(knownClasses);
		} finally {
			db.close();
		}
	}

	private void assertKnownClasses(ReflectClass[] knownClasses) {
		for (int i = 0; i < knownClasses.length; i++) {
			Assert.areNotEqual(fullyQualifiedName(SimpleClass.class), knownClasses[i].getName());
		}
	}

	private String fullyQualifiedName(Class klass) {
		return db4ounit.extensions.util.CrossPlatformServices.fullyQualifiedName(klass);
	}

	private void defrag(String fname) throws IOException {
		DefragmentConfig config = new DefragmentConfig(fname);
		config.storedClassFilter(ignoreClassFilter(SimpleClass.class));
		Defragment.defrag(config);
	}
	
	private StoredClassFilter ignoreClassFilter(final Class klass) {
		return new StoredClassFilter(){
			public boolean accept(StoredClass storedClass) {
				return !storedClass.getName().equals(fullyQualifiedName(klass));
			}
		};
	}

	private String createDatabase() {
		String fname = DB4O_FILE;
		ObjectContainer db = Db4o.openFile(fname);
		try {
			db.store(new SimpleClass("verySimple"));
			db.commit();
		} finally {
			db.close();
		}
		return fname;
		

	}
	

}
