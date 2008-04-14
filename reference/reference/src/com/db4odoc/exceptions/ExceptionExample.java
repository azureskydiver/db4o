/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.exceptions;

import com.db4o.*;
import com.db4o.ext.*;


public class ExceptionExample {
	private static final String DB4O_FILE_NAME = "reference.db4o"; 

	public static void main(String[] args) {
		ObjectContainer container = openDatabase();
		container.close();
		openClient();
		work();
	}
	// end main
	
	private static ObjectContainer openDatabase(){
		ObjectContainer container = null;
		try {
			container = Db4o.openFile(DB4O_FILE_NAME);
		} catch(DatabaseFileLockedException ex) {
			// System.out.println(ex.getMessage());
			// ask the user for a new filename, print
			// or log the exception message
			// and close the application,
			// find and fix the reason
			// and try again
		}
		return container;
	}
	// end openDatabase
	
	private static ObjectContainer openClient(){
		ObjectContainer container = null;
		try {
			container = Db4o.openClient("host", 0xdb40, "user", "password");
		} catch(Db4oIOException ex) {
			//System.out.println(ex.getMessage());
			// ask the user for new connection details, print
			// or log the exception message
			// and close the application,
			// find and fix the reason
			// and try again
		} catch (OldFormatException ex) {
			// see above
		} catch (InvalidPasswordException ex) {
			// see above
		}
		return container;
	}
	// end openClient
	
	private static void work(){
		ObjectContainer container = openDatabase();
		try {
			// do some work with db4o
			container.commit();
		} catch (Db4oException ex){
			// handle exception ....
		} catch (RuntimeException ex){
			// handle exception ....
		} finally {
			container.close();
		}
	}
	// end work
}
