/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.comparing;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.ObjectAttribute;
import com.db4o.query.Query;


public class CompareExample {
	
	private static final String FILENAME = "example.db";

	public static void main(String[] args) {
		configure();
		storeRecords();
		checkRecords();
	}
	// end main

	public static void configure(){
		Db4o.configure().objectClass(MyString.class).compare(new ObjectAttribute() {
            public Object attribute(Object original) {
                if (original instanceof MyString) {
                    return ((MyString) original).toString();
                }
                return original;
            }
        });
	}
	// end configure
	
	public static void storeRecords(){
		new File(FILENAME).delete();
		ObjectContainer container = Db4o.openFile(FILENAME);
		try {
			Record record = new Record("Michael Schumacher, points: 100");
			container.set(record);
			record = new Record("Rubens Barrichello, points: 98");
			container.set(record);
			record = new Record("Kimi Raikonnen, points: 55");
			container.set(record);
		} finally {
			container.close();
		}
	}
	// end storeRecords
	
	public static void checkRecords(){
		ObjectContainer container = Db4o.openFile(FILENAME);
		try {
			Query q = container.query();
			q.constrain(new Record("Rubens"));
	        q.descend("_record").constraints().contains();
			ObjectSet result = q.execute();
			listResult(result);
		} finally {
			container.close();
		}
	}
	// end checkRecords
	
    public static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
    
}
