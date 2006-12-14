/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.defragment;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.defragment.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;

public class SlotDefragmentFixture {

	public static final String PRIMITIVE_FIELDNAME = "_id";
	public static final String WRAPPER_FIELDNAME = "_wrapper";
	public static final String TYPEDOBJECT_FIELDNAME = "_next";
	
	public static class Data {
		
		public int _id;
		public Integer _wrapper;
		public Data _next;

		public Data(int id,Data next) {
			_id = id;
			_wrapper=new Integer(id);
			_next=next;
		}
	}

	public static final int VALUE = 42;

	public static DefragmentConfig defragConfig(boolean forceBackupDelete) {
		DefragmentConfig defragConfig = new DefragmentConfig(SlotDefragmentConstants.FILENAME,SlotDefragmentConstants.BACKUPFILENAME);
		defragConfig.forceBackupDelete(forceBackupDelete);
		return defragConfig;
	}

	public static void createFile(String fileName) {
		Configuration config = Db4o.newConfiguration();
		ObjectContainer db=Db4o.openFile(config,fileName);
		Data data=null;
		for(int value=VALUE-1;value<=VALUE+1;value++) {
			data=new Data(value,data);
			db.set(data);
		}
		db.close();
	}

	public static void forceIndex() {
		Configuration config=Db4o.newConfiguration();
		config.objectClass(Data.class).objectField(PRIMITIVE_FIELDNAME).indexed(true);
		config.objectClass(Data.class).objectField(WRAPPER_FIELDNAME).indexed(true);
		config.objectClass(Data.class).objectField(TYPEDOBJECT_FIELDNAME).indexed(true);
		ObjectContainer db=Db4o.openFile(config,SlotDefragmentConstants.FILENAME);
		Assert.isTrue(db.ext().storedClass(Data.class).storedField(PRIMITIVE_FIELDNAME,Integer.TYPE).hasIndex());
		Assert.isTrue(db.ext().storedClass(Data.class).storedField(WRAPPER_FIELDNAME,Integer.class).hasIndex());
		Assert.isTrue(db.ext().storedClass(Data.class).storedField(TYPEDOBJECT_FIELDNAME,Data.class).hasIndex());
		db.close();
	}

	public static void assertIndex(String fieldName) throws IOException {
		forceIndex();
		Defragment.defrag(SlotDefragmentConstants.FILENAME,SlotDefragmentConstants.BACKUPFILENAME);
		ObjectContainer db=Db4o.openFile(Db4o.newConfiguration(),SlotDefragmentConstants.FILENAME);
		Query query=db.query();
		query.constrain(Data.class);
		query.descend(fieldName).constrain(new Integer(VALUE));
		ObjectSet result=query.execute();
		Assert.areEqual(1,result.size());
		db.close();
	}

	public static void assertDataClassKnown(boolean expected) {
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
