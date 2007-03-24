/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.listdeleting;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;

public class ListDeletingExample {
	public final static String DBFILE = "Test.db";

	public static void main(String[] args) {
		fillUpDb(1);
		deleteTest();
		fillUpDb(1);
		removeAndDeleteTest();
		fillUpDb(1);
		removeTest();
	}

	private static void removeTest() {
		ObjectContainer db = Db4o.openFile(DBFILE);
		try {
			// set update depth to 1 as we only
			// modify List field
			db.ext().configure().objectClass(ListObject.class).updateDepth(1);
			List<ListObject> result = db.<ListObject> query(ListObject.class);
			if (result.size() > 0) {
				// retrieve a ListObject
				ListObject lo1 = result.get(0);
				// remove all the objects from the list
				lo1.getData().removeAll(lo1.getData());
				db.set(lo1);
			}
		} finally {
			db.close();
		}
		// check DataObjects in the list
		// and DataObjects in the database
		db = Db4o.openFile(DBFILE);
		try {
			List<ListObject> result = db.<ListObject> query(ListObject.class);
			if (result.size() > 0) {
				ListObject lo1 = result.get(0);
				System.out.println("DataObjects in the list:  "
						+ lo1.getData().size());
			}
			List<DataObject> removedObjects = db
					.<DataObject> query(DataObject.class);
			System.out.println("DataObjects in the database: "
					+ removedObjects.size());
		} finally {
			db.close();
		}
	}

	// end removeTest

	private static void removeAndDeleteTest() {
		ObjectContainer db = Db4o.openFile(DBFILE);
		try {
			// set update depth to 1 as we only
			// modify List field
			db.ext().configure().objectClass(ListObject.class).updateDepth(1);
			List<ListObject> result = db.<ListObject> query(ListObject.class);
			if (result.size() > 0) {
				// retrieve a ListObject
				ListObject lo1 = result.get(0);
				// create a copy of the objects list
				// to memorize the objects to be deleted
				List tempList = new ArrayList(lo1.getData());
				// remove all the objects from the list
				lo1.getData().removeAll(lo1.getData());
				// and delete them from the database
				Iterator<DataObject> it = tempList.iterator();
				while (it.hasNext()) {
					db.delete(it.next());
				}

				db.set(lo1);
			}
		} finally {
			db.close();
		}
		// check DataObjects in the list
		// and DataObjects in the database
		db = Db4o.openFile(DBFILE);
		try {
			List<ListObject> result = db.<ListObject> query(ListObject.class);
			if (result.size() > 0) {
				ListObject lo1 = result.get(0);
				System.out.println("DataObjects in the list:  "
						+ lo1.getData().size());
			}
			List<DataObject> removedObjects = db
					.<DataObject> query(DataObject.class);
			System.out.println("DataObjects in the database: "
					+ removedObjects.size());
		} finally {
			db.close();
		}
	}

	// end removeAndDeleteTest

	private static void deleteTest() {
		ObjectContainer db = Db4o.openFile(DBFILE);
		try {
			// set cascadeOnDelete in order to delete member objects
			db.ext().configure().objectClass(ListObject.class).cascadeOnDelete(
					true);
			List<ListObject> result = db.<ListObject> query(ListObject.class);
			if (result.size() > 0) {
				// retrieve a ListObject
				ListObject lo1 = result.get(0);
				// delete the ListObject with all the field objects
				db.delete(lo1);
			}
		} finally {
			db.close();
		}
		// check ListObjects and DataObjects in the database
		db = Db4o.openFile(DBFILE);
		try {
			List<ListObject> listObjects = db
					.<ListObject> query(ListObject.class);
			System.out.println("ListObjects in the database:  "
					+ listObjects.size());
			List<DataObject> dataObjects = db
					.<DataObject> query(DataObject.class);
			System.out.println("DataObjects in the database: "
					+ dataObjects.size());
		} finally {
			db.close();
		}
	}

	// end deleteTest

	private static void fillUpDb(int listCount) {
		int dataCount = 50;
		long elapsedTime = 0;
		new File(DBFILE).delete();
		ObjectContainer db = Db4o.openFile(DBFILE);
		try {
			long t1 = System.currentTimeMillis();

			for (int i = 0; i < listCount; i++) {
				ListObject lo = new ListObject();
				lo.setName("list" + String.format("%3d", i));
				for (int j = 0; j < dataCount; j++) {
					DataObject dataObject = new DataObject();
					dataObject.setName("data" + String.format("%5d", j));
					dataObject.setData(System.currentTimeMillis()
							+ " ---- Data Object " + String.format("%5d", j));
					lo.getData().add(dataObject);
				}
				db.set(lo);
			}
			long t2 = System.currentTimeMillis();
			elapsedTime = t2 - t1;
		} finally {
			db.close();
		}
		System.out.println("Completed " + listCount + " lists of " + dataCount
				+ " objects each.");
		System.out.println("Elapsed time: " + elapsedTime + " ms.");
	}
	// end fillUpDb
}
