/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.remote;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.messaging.MessageContext;
import com.db4o.messaging.MessageRecipient;
import com.db4o.messaging.MessageSender;
import com.db4o.query.Candidate;
import com.db4o.query.Evaluation;
import com.db4o.query.Query;

public class RemoteExample {

	private final static String DB4O_FILE_NAME = "reference.db4o";

	public static void main(String[] args) {
		setObjects();
		updateCars();
		setObjects();
		updateCarsWithMessage();
	}

	// end main

	private static void setObjects() {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			for (int i = 0; i < 5; i++) {
				Car car = new Car("car" + i);
				container.set(car);
			}
			container.set(new RemoteExample());
		} finally {
			container.close();
		}
		checkCars();
	}

	// end setObjects

	private static void updateCars() {
		// triggering mass updates with a singleton
		// complete server-side execution
		Configuration configuration = Db4o.newConfiguration();
		configuration.messageLevel(0);
		ObjectServer server = Db4o.openServer(configuration, DB4O_FILE_NAME, 0);
		try {
			ObjectContainer client = server.openClient();
			Query q = client.query();
			q.constrain(RemoteExample.class);
			q.constrain(new Evaluation() {
				public void evaluate(Candidate candidate) {
					// evaluate method is executed on the server
					// use it to run update code
					ObjectContainer objectContainer = candidate
							.objectContainer();
					Query q2 = objectContainer.query();
					q2.constrain(Car.class);
					ObjectSet objectSet = q2.execute();
					while (objectSet.hasNext()) {
						Car car = (Car) objectSet.next();
						car.setModel("Update1-" + car.getModel());
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

	private static void checkCars() {
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			Query q = container.query();
			q.constrain(Car.class);
			ObjectSet objectSet = q.execute();
			listResult(objectSet);
		} finally {
			container.close();
		}
	}
	// end checkCars

	private static void updateCarsWithMessage() {
		Configuration configuration = Db4o.newConfiguration();
		configuration.messageLevel(0);
		ObjectServer server = Db4o.openServer(configuration, DB4O_FILE_NAME, 0xdb40);
		server.grantAccess("user", "password");
		// create message handler on the server
		server.ext().configure().clientServer().setMessageRecipient(
				new MessageRecipient() {
					public void processMessage(
							MessageContext context,
							Object message) {
						// message type defines the code to be
						// executed
						if (message instanceof UpdateServer) {
							Query q = context.container().query();
							q.constrain(Car.class);
							ObjectSet objectSet = q.execute();
							while (objectSet.hasNext()) {
								Car car = (Car) objectSet.next();
								car.setModel("Updated2-"
										+ car.getModel());
								context.container().set(car);
							}
							context.container().commit();
						}
					}
				});
		try {
			ObjectContainer client = Db4o.openClient("localhost", 0xdb40, "user", "password");
			// send message object to the server
			MessageSender sender = client.ext().configure()
					.clientServer().getMessageSender();
			sender.send(new UpdateServer());
			client.close();
		} finally {
			server.close();
		}
		checkCars();
	}
	// end updateCarsWithMessage

	private static void listResult(ObjectSet result) {
		System.out.println(result.size());
		while (result.hasNext()) {
			System.out.println(result.next());
		}
	}
	// end listResult
}
