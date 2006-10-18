/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.symbian;


import java.io.File;
import java.io.IOException;


import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;



public class SymbianTest {

	public static final String YAPFILENAME = "formula1.yap";
	
	public static void main(String[] args) throws IOException {
		setObjects();
		setObjectsSymbian();
		getObjects();
		getObjectsSymbian();
	}
	// end main
	
	public static void setObjects(){
		System.out.println("\nSetting objects using RandomAccessFileAdapter");
		new File(YAPFILENAME).delete();
		Db4o.configure().io(new com.db4o.io.RandomAccessFileAdapter());
		try {
			ObjectContainer db = Db4o.openFile(YAPFILENAME);
			try {
				db.set(new SymbianTest());
			} finally {
				db.close();
			}
		} catch (Exception ex){
			System.out.println("Exception accessing file: " + ex.getMessage());
		}
	}
	// end setObjects

	public static void setObjectsSymbian(){
		System.out.println("\nSetting objects using SymbianIoAdapter");
		new File(YAPFILENAME).delete();
		Db4o.configure().io(new com.db4o.io.SymbianIoAdapter());
		try {
			ObjectContainer db = Db4o.openFile(YAPFILENAME);
			try {
				db.set(new SymbianTest());
			} finally {
				db.close();
			}
		} catch (Exception ex){
			System.out.println("Exception accessing file: " + ex.getMessage());
		}
	}
	// end setObjectsSymbian

	public static void getObjects(){
		System.out.println("\nRetrieving objects using RandomAccessFileAdapter");
		Db4o.configure().io(new com.db4o.io.RandomAccessFileAdapter());
		try {
			ObjectContainer db = Db4o.openFile(YAPFILENAME);
			try {
				 ObjectSet result=db.get(new Object());
				 System.out.println("Objects in the database: " + result.size());
			} finally {
				db.close();
			}
		} catch (Exception ex){
			System.out.println("Exception accessing file: " + ex.getMessage());
		}
	}
	// end getObjects
	
	public static void getObjectsSymbian(){
		System.out.println("\nRetrieving objects using SymbianIoAdapter");
		Db4o.configure().io(new com.db4o.io.SymbianIoAdapter());
		try {
			ObjectContainer db = Db4o.openFile(YAPFILENAME);
			try {
				 ObjectSet result=db.get(new Object());
				 System.out.println("Objects in the database: " + result.size());
			} finally {
				db.close();
			}
		} catch (Exception ex){
			System.out.println("Exception accessing file: " + ex.getMessage());
		}
	}
	// end getObjectsSymbian
}

