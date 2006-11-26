/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.defragment;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.defragment.*;
import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4o.test.util.*;

import db4ounit.*;

public class SlotDefragmentTestCase implements TestLifeCycle {

	private static final String FIELDNAME = "_id";

	public static class Data {
		
		public int _id;

		public Data(int id) {
			_id = id;
		}
	}

	private static final int VALUE = 42;
	
	/**
	 * @sharpen.ignore
	 */
	public void testSkipsClass() throws Exception {
		DefragmentConfig defragConfig = defragConfig(true);
		Defragment.defrag(defragConfig);
		assertDataClassKnown(true);

		defragConfig = defragConfig(true);
		defragConfig.storedClassFilter(new AvailableClassFilter());
		Defragment.defrag(defragConfig);
		assertDataClassKnown(true);

		defragConfig = defragConfig(true);
		Vector excluded=new Vector();
		excluded.add(Data.class.getName());
		ExcludingClassLoader loader=new ExcludingClassLoader(getClass().getClassLoader(),excluded);
		defragConfig.storedClassFilter(new AvailableClassFilter(loader));
		Defragment.defrag(defragConfig);
		assertDataClassKnown(false);
	}

	public void _testPrimitiveIndex() throws Exception {
		forceIndex();
		Defragment.defrag(SlotDefragmentConstants.FILENAME,SlotDefragmentConstants.BACKUPFILENAME);
		ObjectContainer db=Db4o.openFile(Db4o.newConfiguration(),SlotDefragmentConstants.FILENAME);
		Query query=db.query();
		query.constrain(Data.class);
		query.descend(FIELDNAME).constrain(new Integer(VALUE));
		ObjectSet result=query.execute();
		Assert.areEqual(1,result.size());
		db.close();
	}

	public void testNoForceDelete() throws Exception {
		Defragment.defrag(SlotDefragmentConstants.FILENAME,SlotDefragmentConstants.BACKUPFILENAME);
		Assert.expect(IOException.class, new CodeBlock() {
			public void run() throws Exception {
				Defragment.defrag(SlotDefragmentConstants.FILENAME,SlotDefragmentConstants.BACKUPFILENAME);
			}
		});
	}	

	public void setUp() throws Exception {
		new File(SlotDefragmentConstants.FILENAME).delete();
		new File(SlotDefragmentConstants.BACKUPFILENAME).delete();
		createFile(SlotDefragmentConstants.FILENAME);
	}

	public void tearDown() throws Exception {
	}

	private DefragmentConfig defragConfig(boolean forceBackupDelete) {
		DefragmentConfig defragConfig = new DefragmentConfig(SlotDefragmentConstants.FILENAME,SlotDefragmentConstants.BACKUPFILENAME);
		defragConfig.forceBackupDelete(forceBackupDelete);
		return defragConfig;
	}

	private void createFile(String fileName) {
		Configuration config = Db4o.newConfiguration();
		ObjectContainer db=Db4o.openFile(config,fileName);
		db.set(new Data(VALUE));
		db.close();
	}

	private void forceIndex() {
		Configuration config=Db4o.newConfiguration();
		config.objectClass(Data.class).objectField(FIELDNAME).indexed(true);
		ObjectContainer db=Db4o.openFile(config,SlotDefragmentConstants.FILENAME);
		Assert.isTrue(db.ext().storedClass(Data.class).storedField("_id",Integer.TYPE).hasIndex());
		db.close();
	}

	private void assertDataClassKnown(boolean expected) {
		ObjectContainer db=Db4o.openFile(Db4o.newConfiguration(),SlotDefragmentConstants.FILENAME);
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
