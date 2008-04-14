/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.comparing;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;


public class CompareExample {
	
	private static final String DB4O_FILE_NAME = "reference.db4o";

	public static void main(String[] args) {
		Configuration configuration = configure();
		storeRecords(configuration);
		checkRecords(configuration);
	}
	// end main

	private static Configuration configure(){
		Configuration configuration = Db4o.newConfiguration();
		configuration.objectClass(MyString.class).compare(new ObjectAttribute() {
            public Object attribute(Object original) {
                if (original instanceof MyString) {
                    return ((MyString) original).toString();
                }
                return original;
            }
        });
		return configuration;
	}
	// end configure
	
	private static void storeRecords(Configuration configuration){
		new File(DB4O_FILE_NAME).delete();
		ObjectServer server = Db4o.openServer(configuration, DB4O_FILE_NAME, 0xdb40);
		server.grantAccess("A", "A");
		//ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		//ObjectContainer container = server.openClient();
		ObjectContainer container = Db4o.openClient("localhost", 0xdb40, "A", "A");
		//ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			Record record = new Record("Michael Schumacher, points: 100");
			container.store(record);
			record = new Record("Rubens Barrichello, points: 98");
			container.store(record);
			record = new Record("Kimi Raikonnen, points: 55");
			container.store(record);
		} finally {
			container.close();
			server.close();
		}
	}
	// end storeRecords
	
	private static void checkRecords(Configuration configuration){
		ObjectServer server = Db4o.openServer(configuration, DB4O_FILE_NAME, 0xdb40);
		server.grantAccess("A", "A");
		//ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		//ObjectContainer container = server.openClient();
		ObjectContainer container = Db4o.openClient("localhost", 0xdb40, "A", "A");
		//ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			Query q = container.query();
			q.constrain(new Record("Rubens"));
	        q.descend("_record").constraints().contains();
			ObjectSet result = q.execute();
			listResult(result);
		} finally {
			container.close();
			server.close();
		}
	}
	// end checkRecords
	
	private static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
    
}
