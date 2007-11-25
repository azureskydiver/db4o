/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.aliases;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;


public class InterLanguageExample {

	private static final String DB4O_FILE_NAME = "reference.db4o";
	
	public static void main(String[] args) {
		saveObjects();
	}
	// end main

	private static void saveObjects(){
		new File(DB4O_FILE_NAME ).delete();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			Pilot pilot = new Pilot("David Barrichello",99);
			container.set(pilot);
			pilot = new Pilot("Michael Schumacher",100);
			container.set(pilot);
		} finally {
			container.close();
		}
	}
	// end saveObjects
}
