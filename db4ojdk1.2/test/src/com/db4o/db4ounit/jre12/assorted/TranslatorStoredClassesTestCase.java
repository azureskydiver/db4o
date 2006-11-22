/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.assorted;

import java.io.*;
import java.math.*;

import com.db4o.*;
import com.db4o.config.*;

import db4ounit.*;

public class TranslatorStoredClassesTestCase implements TestCase {

	private final static String FILENAME="translator.yap";
	
	public static class DataRawChild implements Serializable {
		public int _id;

		public DataRawChild(int id) {
			_id=id;
		}
	}

	public static class DataRawParent {
		public DataRawChild _child;

		public DataRawParent(int id) {
			_child=new DataRawChild(id);
		}
	}

	public static class DataBigDecimal {
		public BigDecimal _bd;

		public DataBigDecimal(int id) {
			_bd=new BigDecimal(String.valueOf(id));
		}
	}
	
	public void testBigDecimal() {
		assertStoredClassesAfterTranslator(BigDecimal.class,new DataBigDecimal(42));
	}

	public void testRaw() {
		assertStoredClassesAfterTranslator(DataRawChild.class,new DataRawParent(42));
	}

	public void assertStoredClassesAfterTranslator(Class translated,Object data) {
		createFile(translated,data);
		check(translated);
	}

	private static void createFile(Class translated,Object data) {
		new File(FILENAME).delete();
        ObjectContainer server = db(translated,new TSerializable());
        server.set(data);
        server.close();
	}

	private static void check(Class translated) {
		ObjectContainer db=db(translated,null);
		db.ext().storedClasses();
		db.close();
	}

	private static ObjectContainer db(Class translated,ObjectTranslator translator) {
		Configuration config=Db4o.newConfiguration();
		config.objectClass(translated).translate(translator);
		return Db4o.openFile(config,FILENAME);
	}

}
