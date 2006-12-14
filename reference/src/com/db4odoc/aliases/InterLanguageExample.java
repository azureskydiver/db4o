/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.aliases;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;


public class InterLanguageExample {

	private static final String YAPFILENAME = "formula1.yap";
	
	public static void main(String[] args) {
		saveObjects();
	}
	// end main

	public static void saveObjects(){
		new File(YAPFILENAME ).delete();
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			Pilot pilot = new Pilot("David Barrichello",99);
			db.set(pilot);
			pilot = new Pilot("Michael Schumacher",100);
			db.set(pilot);
		} finally {
			db.close();
		}
	}
	// end saveObjects
}
