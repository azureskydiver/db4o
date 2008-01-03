/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.selectivepersistence;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

public class TransientClassExample {

	private final static String DB4O_FILE_NAME = "reference.db4o";

	public static void main(String[] args) {
		saveObjects();
		retrieveObjects();
	}

	// end main

	private static void saveObjects() {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			// Save Test1 object with a NotStorable class field
			Test1 test1 = new Test1("Test1", new NotStorable());
			container.store(test1);
			// Save Test2 object with a NotStorable class field
			Test2 test2 = new Test2("Test2", new NotStorable(), test1);
			container.store(test2);
		} finally {
			container.close();
		}
	}

	// end saveObjects

	private static void retrieveObjects() {
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			// retrieve the results and check if the NotStorable
			// instances were saved
			ObjectSet result = container.queryByExample(null);
			listResult(result);
		} finally {
			container.close();
		}
	}

	// end retrieveObjects

	private static void listResult(ObjectSet result) {
		System.out.println(result.size());
		for (int x = 0; x < result.size(); x++)
			System.out.println(result.get(x));
	}
	// end listResult

}
