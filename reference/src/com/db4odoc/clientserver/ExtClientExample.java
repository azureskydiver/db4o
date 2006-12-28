/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.clientserver;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.ObjectSet;
import com.db4o.ext.ExtClient;


public class ExtClientExample  {

	public final static String EXTFILENAME="formula1e.yap";
	 public final static String YAPFILENAME="formula1.yap";
	
	public static void main(String[] args) {
		switchExtClients();
	}
	// end main

    public static void switchExtClients() {
    	new File(YAPFILENAME).delete();
    	new File(EXTFILENAME).delete();
        ObjectServer server=Db4o.openServer(YAPFILENAME,0);
        try {
            ObjectContainer client=server.openClient();
            deleteAll(client); // added to solve sticking objects in doctor 
            Car car = new Car("BMW");
            client.set(car);
            System.out.println("Objects in the main database file:");
            retrieveAll(client);
			
			System.out.println("Switching to additional database:");
            ExtClient clientExt = (ExtClient)client;
            clientExt.switchToFile(EXTFILENAME);
            car = new Car("Ferrari");
            clientExt.set(car);
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
    
    public static void retrieveAll(ObjectContainer db){
        ObjectSet result=db.get(new Object());
        listResult(result);
    }
    // end retrieveAll
    
    public static void deleteAll(ObjectContainer db) {
        ObjectSet result=db.get(new Object());
        while(result.hasNext()) {
            db.delete(result.next());
        }
    }
    // end deleteAll
    
    public static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
    