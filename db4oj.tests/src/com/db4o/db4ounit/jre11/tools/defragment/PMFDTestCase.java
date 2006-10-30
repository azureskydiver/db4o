/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre11.tools.defragment;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.reflect.*;
import com.db4o.tools.defragment.*;

import db4ounit.*;

public class PMFDTestCase implements TestLifeCycle {
	private final static String SOURCEFILE = "original.yap";
	private final static String TARGETFILE = "copied.yap";
	private final static String MAPPINGFILE = "mapping.yap";

	private static final int NUM_ENTRIES = 5;

	public static void main(String[] args) {
		new TestRunner(new ReflectionTestSuiteBuilder(PMFDTestCase.class)).run();
	}
	
	public void setUp() throws Exception {
		createSource(SOURCEFILE);
		deleteFile(MAPPINGFILE);
		deleteFile(TARGETFILE);
	}

	public void tearDown() throws Exception {
	}

	public void testDefrag() throws IOException {
		long start=System.currentTimeMillis();
		PMFD.defrag(SOURCEFILE,TARGETFILE,MAPPINGFILE);
		System.out.println("TIME "+(System.currentTimeMillis()-start)+" ms");
		checkCopied();
	}

	private static Configuration config() {
		Configuration config=Db4o.newConfiguration();
		ObjectClass clazz=config.objectClass(Data.class);
		clazz.objectField("_id").indexed(true);
		clazz.objectField("_name").indexed(true);
		return config;
	}
	
	private static void createSource(String fileName) {
		deleteFile(fileName);
		ObjectContainer db = Db4o.openFile(config(),fileName);
		Data data = null;
		for (int i = 0; i < NUM_ENTRIES; i++) {
			String name = "X" + i;
			data = new Data(i, name, data, new Data[] { data, data });
			db.set(data);
			if(i%100000==0) {
				db.commit();
			}
		}
		checkStoredClasses(db);
		db.close();
	}

	private static void deleteFile(String path) {
		new File(path).delete();
	}

	private static void checkCopied() {
		try {
			ObjectContainer db = Db4o.openFile(config(),TARGETFILE);
			System.out.println("IDENTITY: "+db.ext().identity());
			try {
				System.out.println(db);
				checkStoredClasses(db);
				checkQuery(db);
			} finally {
				db.close();
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	private static void checkQuery(ObjectContainer db) {
		ObjectSet all = db.query((Class) null);
		System.out.println(all.size());
		while (all.hasNext()) {
			Data data = (Data) all.next();
			System.out.println(data + " <- " + data._previous);
		}
	}

	private static void checkStoredClasses(ObjectContainer db) {
		System.out.println("CLASS COLLECTION: "+((YapFile)db).classCollection().getID());
		StoredClass[] storedClasses = db.ext().storedClasses();
		System.out.println("STORED CLASSES: "+storedClasses.length);
		for (int classIdx = 0; classIdx < storedClasses.length; classIdx++) {
			StoredClass curClass = storedClasses[classIdx];
			long[] ids = curClass.getIDs();
			System.out.println(curClass.getName() + " : (" +((YapClass)curClass).getID()+") " + ids.length);
			StringBuffer fieldList = new StringBuffer();
			StoredField[] fields = curClass.getStoredFields();
			for (int fieldIdx = 0; fieldIdx < fields.length; fieldIdx++) {
				if (fieldList.length() > 0) {
					fieldList.append(',');
				}
				StoredField curField = fields[fieldIdx];
				ReflectClass curType = curField.getStoredType();
				fieldList.append(curField.getName() + ":"
						+ (curType == null ? "?" : curType.getName()));
				fieldList.append(":"+((YapField)curField).hasIndex());
			}
			System.out.println(fieldList);
			for (int idIdx = 0; idIdx < ids.length; idIdx++) {
				Object obj = db.ext().getByID(ids[idIdx]);
				db.ext().activate(obj, Integer.MAX_VALUE);
				System.out.println(ids[idIdx] + ": " + obj
						+ (obj == null ? "" : " / " + obj.getClass()));
			}
		}
	}
}
