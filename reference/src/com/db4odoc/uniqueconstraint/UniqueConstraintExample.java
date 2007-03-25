/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.uniqueconstraint;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.config.Configuration;
import com.db4o.constraints.UniqueFieldValueConstraint;
import com.db4o.constraints.UniqueFieldValueConstraintViolationException;


public class UniqueConstraintExample {
	private static final String FILENAME = "test.db"; 

	public static void main(String[] args) {
		configure();
		storeObjects();
	}
	// end main

	private static Configuration configure(){
		Configuration configuration = Db4o.newConfiguration();
		configuration.objectClass(Pilot.class).objectField("name").indexed(true);
		configuration.add(new UniqueFieldValueConstraint(Pilot.class, "name"));
		return configuration;
	}
	// end configure
	
	private static void storeObjects(){
		new File(FILENAME).delete();
		ObjectServer server = Db4o.openServer(configure(), FILENAME, 0);
		Pilot pilot1 = null;
		Pilot pilot2 = null;
		try {
			ObjectContainer client1 = server.openClient();
			try {
				// creating and storing pilot1 to the database
				pilot1 = new Pilot("Rubens Barichello",99);
				client1.set(pilot1);
				ObjectContainer client2 = server.openClient();
				try {
					// creating and storing pilot2 to the database
					pilot2 = new Pilot("Rubens Barichello",100);
					client2.set(pilot2);
					// commit the changes
					client2.commit();
				} catch (UniqueFieldValueConstraintViolationException ex){
					System.out.println("Unique constraint violation in client2 saving: " + pilot1);
					client2.rollback();
				} finally {
					client2.close();
				}
				// Pilot Rubens Barichello is already in the database,
				// commit will fail
				client1.commit();
			} catch (UniqueFieldValueConstraintViolationException ex){
				System.out.println("Unique constraint violation client1 saving: " + pilot2);
				client1.rollback();
			} finally {
				client1.close();
			}
		} finally {
			server.close();
		}
	}
	// end storeObjects
	
}
