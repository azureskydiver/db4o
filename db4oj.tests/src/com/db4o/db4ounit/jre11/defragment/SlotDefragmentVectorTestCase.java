/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre11.defragment;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.db4ounit.common.defragment.*;
import com.db4o.defragment.*;
import com.db4o.query.*;

import db4ounit.*;

public class SlotDefragmentVectorTestCase implements TestCase {

	public static class Holder {
		public Vector _vector;

		public Holder(Vector vector) {
			this._vector = vector;
		}
	}

	public static class Data {
		public int _id;

		public Data(int id) {
			this._id = id;
		}
	}

	private static final int NUMVECTORS = 10;
	private static final int NUMENTRIES = 10;
	
	public void testVectorDefragment() throws Exception {
		store();
		defrag();
		query();
	}

	private void query() {
		ObjectContainer db=Db4o.openFile(Db4o.newConfiguration(),SlotDefragmentTestConstants.FILENAME);
		Query query=db.query();
		query.constrain(Holder.class);
		ObjectSet result=query.execute();
		Assert.areEqual(NUMVECTORS+1,result.size());
		db.close();
	}

	private void defrag() throws IOException {
		DefragmentConfig config=new DefragmentConfig(SlotDefragmentTestConstants.FILENAME);
		config.forceBackupDelete(true);
		Defragment.defrag(config);
	}

	private void store() {
		new File(SlotDefragmentTestConstants.FILENAME).delete();
		ObjectContainer db=Db4o.openFile(Db4o.newConfiguration(),SlotDefragmentTestConstants.FILENAME);
		for(int vectorIdx=0;vectorIdx<NUMVECTORS;vectorIdx++) {
			Vector vector=new Vector(NUMENTRIES);
			for(int entryIdx=0;entryIdx<NUMENTRIES;entryIdx++) {
				Object obj=(entryIdx%2==0 ? (Object)new Data(entryIdx) : (Object)String.valueOf(entryIdx));
				vector.addElement(obj);
			}
			db.set(new Holder(vector));
		}
		db.set(new Holder(new Vector(0)));
		db.close();
	}
}
