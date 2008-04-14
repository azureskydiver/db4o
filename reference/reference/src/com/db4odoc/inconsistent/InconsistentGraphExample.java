package com.db4odoc.inconsistent;

import java.io.*;

import com.db4o.*;
import com.db4o.ext.*;

public class InconsistentGraphExample {

	private static final String DB4O_FILE_NAME = "reference.db4o";

	private static final int PORT = 4440;

	private static final String USER = "db4o";

	private static final String PASSWORD = "db4o";


	public static void main(String[] args) throws IOException {
		new InconsistentGraphExample().run();
	}

	// end main

	public void run() throws IOException, DatabaseFileLockedException {
		new File(DB4O_FILE_NAME).delete();
		ObjectServer server = Db4o.openServer(DB4O_FILE_NAME, PORT);
		try {
			server.grantAccess(USER, PASSWORD);

			ObjectContainer client1 = server.openClient();
			ObjectContainer client2 = server.openClient();

			if (client1 != null && client2 != null) {
				try {
					// wait for the operations to finish
					waitForCompletion();

					// save pilot with client1
					Car client1Car = new Car("Ferrari", 2006, new Pilot(
							"Schumacher"));
					client1.store(client1Car);
					client1.commit();
					System.out.println("Client1 version initially: " + client1Car);
					waitForCompletion();

					// retrieve the same pilot with client2
					Car client2Car = (Car) client2.query(Car.class).next();
					System.out.println("Client2 version initially: " + client2Car);

					// delete the pilot with client1
					Pilot client1Pilot = (Pilot)client1.query(Pilot.class).next();
					client1.delete(client1Pilot);
					// modify the car, add and link a new pilot with client1
					client1Car.setModel(2007);
					client1Car.setPilot(new Pilot("Hakkinnen"));
					client1.store(client1Car);
					client1.commit();

					waitForCompletion();
					client1Car = (Car) client1.query(Car.class).next();
					System.out.println("Client1 version after update: " + client1Car);


					System.out.println();
					System.out.println("client2Car still holds the old object graph in its reference cache"); 
					client2Car = (Car) client2.query(Car.class).next();
					System.out.println("Client2 version after update: " + client2Car);
					ObjectSet result = client2.query(Pilot.class);
					System.out.println("Though the new Pilot is retrieved by a new query: ");
					listResult(result);

					waitForCompletion();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					closeClient(client1);
					closeClient(client2);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			server.close();
		}
	}

	// end run

	private void closeClient(ObjectContainer client) {
		client.close();
	}

	// end closeClient

	private ObjectContainer openClient() {
		try {
			ObjectContainer client = Db4o.openClient("localhost", PORT, USER,
					PASSWORD);
			return client;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	// end openClient

	
	private void waitForCompletion() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	// end waitForCompletion
	
	private static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult

}