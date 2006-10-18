/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.remote;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.ObjectSet;
import com.db4o.messaging.MessageRecipient;
import com.db4o.messaging.MessageSender;
import com.db4o.query.Candidate;
import com.db4o.query.Evaluation;
import com.db4o.query.Query;


public class RemoteExample  {
	public final static String YAPFILENAME="formula1.yap";
	public static void main(String[] args) {
		setObjects();
		updateCars();
		setObjects();
		updateCarsWithMessage();
	}
	// end main

	public static void setObjects(){
		new File(YAPFILENAME).delete();
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			for (int i = 0; i < 5; i++) {
				Car car = new Car("car"+i);
	            db.set(car);
	        }
			db.set(new RemoteExample());
		} finally {
			db.close();
		}
		checkCars();
	}
	// end setObjects
	
	
	public static void updateCars(){
		// triggering mass updates with a singleton
        // complete server-side execution
		ObjectServer server=Db4o.openServer(YAPFILENAME,0);
		server.ext().configure().messageLevel(0);
        try {
            ObjectContainer client=server.openClient();
            Query q = client.query();
            q.constrain(RemoteExample.class);
            q.constrain(new Evaluation() {
            	public void evaluate(Candidate candidate) {
            		// evaluate method is executed on the server
            		// use it to run update code
            		ObjectContainer objectContainer = candidate.objectContainer();
            		Query q2 = objectContainer.query();
            		q2.constrain(Car.class);
            		ObjectSet objectSet = q2.execute();
            		while(objectSet.hasNext()){
            			Car car = (Car)objectSet.next();
            			car.setModel( "Update1-"+ car.getModel());
            			objectContainer.set(car);
            		}
            		objectContainer.commit();
            	}
            });
            q.execute();
            client.close();
        } finally {
        	server.close();
        }
        checkCars();
	}
	// end updateCars
	
	private static void checkCars(){
        ObjectContainer db = Db4o.openFile(YAPFILENAME);
        try {
	        Query q = db.query();
	        q.constrain(Car.class);
	        ObjectSet objectSet = q.execute();
	        listResult(objectSet);
        } finally {
        	db.close();
        }
    }
	// end checkCars
	
	 public static void updateCarsWithMessage() {
		ObjectServer server = Db4o.openServer(YAPFILENAME, 0);
		server.ext().configure().messageLevel(0);
		// create message handler on the server
		server.ext().configure().setMessageRecipient(new MessageRecipient() {
			public void processMessage(ObjectContainer objectContainer,
					Object message) {
				// message type defines the code to be executed
				if (message instanceof UpdateServer) {
					Query q = objectContainer.query();
					q.constrain(Car.class);
					ObjectSet objectSet = q.execute();
					while (objectSet.hasNext()) {
						Car car = (Car) objectSet.next();
						car.setModel("Updated2-" + car.getModel());
						objectContainer.set(car);
					}
					objectContainer.commit();
				}
			}
		});
		try {
			ObjectContainer client = server.openClient();
			// send message object to the server
			MessageSender sender = client.ext().configure().getMessageSender();
			sender.send(new UpdateServer());
			client.close();
		} finally {
			server.close();
		}
		checkCars();
	}
	 // end updateCarsWithMessage
	 
	    public static void listResult(ObjectSet result) {
	        System.out.println(result.size());
	        while(result.hasNext()) {
	            System.out.println(result.next());
	        }
	    }
	    // end listResult
}
