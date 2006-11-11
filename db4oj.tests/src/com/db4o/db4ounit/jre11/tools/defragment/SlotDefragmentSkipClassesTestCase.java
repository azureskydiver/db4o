/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre11.tools.defragment;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.test.util.*;
import com.db4o.tools.defragment.*;

import db4ounit.*;

public class SlotDefragmentSkipClassesTestCase implements TestCase {

	public final static String FILENAME="defrag.yap";
	
	public static class Data {
		public int _id;

		public Data(int _id) {
			this._id = _id;
		}
	}
	
	public void testSkipsClass() throws Exception {
		new File(FILENAME).delete();
		ObjectContainer db=Db4o.openFile(Db4o.newConfiguration(),FILENAME);
		db.set(new Data(42));
		db.close();
		
		DefragmentConfig defragConfig = new DefragmentConfig(FILENAME);
		SlotDefragment.defrag(defragConfig);
		assertDataClassKnown(true);

		defragConfig = new DefragmentConfig(FILENAME);
		defragConfig.yapClassFilter(new AvailableClassFilter(getClass().getClassLoader()));
		SlotDefragment.defrag(defragConfig);
		assertDataClassKnown(true);

		Vector excluded=new Vector();
		excluded.add(Data.class.getName());
		ExcludingClassLoader loader=new ExcludingClassLoader(getClass().getClassLoader(),excluded);
		defragConfig.yapClassFilter(new AvailableClassFilter(loader));
		SlotDefragment.defrag(defragConfig);
		assertDataClassKnown(false);
	}

	private void assertDataClassKnown(boolean expected) {
		ObjectContainer db=Db4o.openFile(Db4o.newConfiguration(),FILENAME);
		try {
			StoredClass storedClass=db.ext().storedClass(Data.class);
			if(expected) {
				Assert.isNotNull(storedClass);
			}
			else {
				Assert.isNull(storedClass);
			}
		}
		finally {
			db.close();
		}
	}
}
