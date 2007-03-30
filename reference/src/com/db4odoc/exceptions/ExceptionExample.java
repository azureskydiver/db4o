/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.exceptions;

import java.io.IOException;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ext.DatabaseFileLockedException;
import com.db4o.ext.Db4oException;


public class ExceptionExample {
	private static final String FILENAME = "test.db"; 

	public static void main(String[] args) {
		ObjectContainer db = openDatabase();
		db.close();
		openClient();
		work();
	}
	// end main
	
	public static ObjectContainer openDatabase(){
		ObjectContainer db = null;
		try {
			db = Db4o.openFile(FILENAME);
		} catch(DatabaseFileLockedException ex) {
			// System.out.println(ex.getMessage());
			// ask the user for a new filename, print
			// or log the exception message
			// and close the application,
			// find and fix the reason
			// and try again
		}
		return db;
	}
	// end openDatabase
	
	public static ObjectContainer openClient(){
		ObjectContainer db = null;
		try {
			db = Db4o.openClient("host", 0xdb40, "user", "password");
		} catch(IOException ex) {
			//System.out.println(ex.getMessage());
			// ask the user for new connection details, print
			// or log the exception message
			// and close the application,
			// find and fix the reason
			// and try again
		}
		return db;
	}
	// end openClient
	
	public static void work(){
		ObjectContainer db = openDatabase();
		try {
			// do some work with db4o
			db.commit();
		} catch (Db4oException ex){
			// handle exception ....
		} catch (RuntimeException ex){
			// handle exception ....
		} finally {
			db.close();
		}
	}
	// end work
}
