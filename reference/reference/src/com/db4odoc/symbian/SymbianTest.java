/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.symbian;

import java.io.*;

import com.db4o.*;

public class SymbianTest {

	private static final String DB4O_FILE_NAME = "reference.db4o";

	public static void main(String[] args) throws IOException {
		setObjects();
		setObjectsSymbian();
		getObjects();
		getObjectsSymbian();
	}

	// end main

	private static void setObjects() {
		System.out
				.println("\nSetting objects using RandomAccessFileAdapter");
		new File(DB4O_FILE_NAME).delete();
		Db4o.configure()
				.io(new com.db4o.io.RandomAccessFileAdapter());
		try {
			ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
			try {
				container.store(new SymbianTest());
			} finally {
				container.close();
			}
		} catch (Exception ex) {
			System.out.println("Exception accessing file: "
					+ ex.getMessage());
		}
	}

	// end setObjects

	private static void setObjectsSymbian() {
		System.out
				.println("\nSetting objects using SymbianIoAdapter");
		new File(DB4O_FILE_NAME).delete();
		Db4o.configure().io(new com.db4o.io.SymbianIoAdapter());
		try {
			ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
			try {
				container.store(new SymbianTest());
			} finally {
				container.close();
			}
		} catch (Exception ex) {
			System.out.println("Exception accessing file: "
					+ ex.getMessage());
		}
	}

	// end setObjectsSymbian

	private static void getObjects() {
		System.out
				.println("\nRetrieving objects using RandomAccessFileAdapter");
		Db4o.configure()
				.io(new com.db4o.io.RandomAccessFileAdapter());
		try {
			ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
			try {
				ObjectSet result = container.queryByExample(new Object());
				System.out.println("Objects in the database: "
						+ result.size());
			} finally {
				container.close();
			}
		} catch (Exception ex) {
			System.out.println("Exception accessing file: "
					+ ex.getMessage());
		}
	}

	// end getObjects

	private static void getObjectsSymbian() {
		System.out
				.println("\nRetrieving objects using SymbianIoAdapter");
		Db4o.configure().io(new com.db4o.io.SymbianIoAdapter());
		try {
			ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
			try {
				ObjectSet result = container.queryByExample(new Object());
				System.out.println("Objects in the database: "
						+ result.size());
			} finally {
				container.close();
			}
		} catch (Exception ex) {
			System.out.println("Exception accessing file: "
					+ ex.getMessage());
		}
	}
	// end getObjectsSymbian
}
