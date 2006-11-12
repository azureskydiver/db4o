/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre11.tools.defragment;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.test.util.*;
import com.db4o.tools.defragment.*;

import db4ounit.*;

public class SlotDefragmentTestCase implements TestLifeCycle {

	public final static String FILENAME="defrag.yap";
	public final static String BACKUPFILENAME=FILENAME+".backup";
	
	public static class Data {
		public int _id;

		public Data(int _id) {
			this._id = _id;
		}
	}
	
	public void testSkipsClass() throws Exception {
		DefragmentConfig defragConfig = defragConfig(true);
		SlotDefragment.defrag(defragConfig);
		assertDataClassKnown(true);

		defragConfig = defragConfig(true);
		defragConfig.storedClassFilter(new AvailableClassFilter());
		SlotDefragment.defrag(defragConfig);
		assertDataClassKnown(true);

		defragConfig = defragConfig(true);
		Vector excluded=new Vector();
		excluded.add(Data.class.getName());
		ExcludingClassLoader loader=new ExcludingClassLoader(getClass().getClassLoader(),excluded);
		defragConfig.storedClassFilter(new AvailableClassFilter(loader));
		SlotDefragment.defrag(defragConfig);
		assertDataClassKnown(false);
	}

	public void testNoForceDelete() throws Exception {
		SlotDefragment.defrag(FILENAME,BACKUPFILENAME);
		Assert.expect(IOException.class, new CodeBlock() {
			public void run() throws Exception {
				SlotDefragment.defrag(FILENAME,BACKUPFILENAME);
			}
		});
	}	

	public void setUp() throws Exception {
		new File(FILENAME).delete();
		new File(BACKUPFILENAME).delete();
		createFile(FILENAME);
	}

	public void tearDown() throws Exception {
	}

	private DefragmentConfig defragConfig(boolean forceBackupDelete) {
		DefragmentConfig defragConfig = new DefragmentConfig(FILENAME,BACKUPFILENAME);
		defragConfig.forceBackupDelete(forceBackupDelete);
		return defragConfig;
	}

	private void createFile(String fileName) {
		ObjectContainer db=Db4o.openFile(Db4o.newConfiguration(),fileName);
		db.set(new Data(42));
		db.close();
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
