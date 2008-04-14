/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.clientserver;

import java.io.*;

import com.db4o.*;
import com.db4o.ext.*;


public class ExtClientExample  {

	private final static String EXTFILENAME="reference_e.db4o";
	 private final static String DB4O_FILE_NAME="reference.db4o";
	
	public static void main(String[] args) {
		switchExtClients();
	}
	// end main

	private static void switchExtClients() {
    	new File(DB4O_FILE_NAME).delete();
    	new File(EXTFILENAME).delete();
        ObjectServer server=Db4o.openServer(DB4O_FILE_NAME,0);
        try {
            ObjectContainer client=server.openClient();
            deleteAll(client); // added to solve sticking objects in doctor 
            Car car = new Car("BMW");
            client.store(car);
            System.out.println("Objects in the main database file:");
            retrieveAll(client);
			
			System.out.println("Switching to additional database:");
            ExtClient clientExt = (ExtClient)client;
            clientExt.switchToFile(EXTFILENAME);
            car = new Car("Ferrari");
            clientExt.store(car);
            retrieveAll(clientExt);
			System.out.println("Main database file again: ");
			clientExt.switchToMainFile();
			retrieveAll(clientExt);
			clientExt.close();
        }
        finally {
            server.close();
        }
    }
    // end switchExtClients
    
	private static void retrieveAll(ObjectContainer container){
        ObjectSet result=container.queryByExample(new Object());
        listResult(result);
    }
    // end retrieveAll
    
	private static void deleteAll(ObjectContainer container) {
        ObjectSet result=container.queryByExample(new Object());
        while(result.hasNext()) {
            container.delete(result.next());
        }
    }
    // end deleteAll
    
	private static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
    