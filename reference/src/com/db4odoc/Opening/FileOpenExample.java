/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.Opening;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

public class FileOpenExample {
	public final static String YAPFILENAME="formula1.yap";

	public static void main(String[] args) {
		setObjects();
		getCars();
	}
	// end main

	public static void setObjects(){
		new File(YAPFILENAME).delete();
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			Car car = new Car("BMW", new Pilot("Rubens Barrichello"));
			db.set(car);
			car = new Car("Ferrari", new Pilot("Michael Schumacher"));
			db.set(car);
		} finally {
			db.close();
		}
	}
	// end setObjects
	
	public static void getCars() {
		Db4o.configure().readOnly(true);
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			ObjectSet result = db.query(Car.class);
			listResult(result);
			getPilots();
		} finally {
			db.close();
		}
	}	
	// end getCars
	
	public static void getPilots() {
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			ObjectSet result = db.query(Pilot.class);
			listResult(result);
		} finally {
			db.close();
		}
	}	
	// end getPilots
	
    public static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
