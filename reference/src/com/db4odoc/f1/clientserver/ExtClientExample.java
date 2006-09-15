/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.clientserver;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.ext.ExtClient;
import com.db4odoc.f1.Util;


public class ExtClientExample extends Util {

	public final static String EXTFILENAME="formula1e.yap";
	
	public static void main(String[] args) {
		switchExtClients();
	}

    public static void switchExtClients() {
    	new File(Util.YAPFILENAME).delete();
    	new File(EXTFILENAME).delete();
        ObjectServer server=Db4o.openServer(Util.YAPFILENAME,0);
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
        }
        finally {
            server.close();
        }
    }
}
    