/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.reflections;

import java.io.File;
import java.io.IOException;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectConstructor;
import com.db4o.reflect.ReflectField;
import com.db4o.reflect.ReflectMethod;
import com.db4o.reflect.generic.GenericReflector;

public class ReflectorExample  {
	
	private final static String DB4O_FILE_NAME="reference.db4o";
	
	public static void main(String[] args) throws IOException {
		setCars();
		testReflector();
//		getReflectorInfo();
//		getCars();
//		getCarInfo();
	}
	// end main
	
	private static void setCars()
	{
		 new File(DB4O_FILE_NAME).delete();
		 ObjectContainer container=Db4o.openFile(DB4O_FILE_NAME);
		try {
			Car car1 = new Car("BMW");
			container.store(car1);
			Car car2 = new Car("Ferrari");
			container.store(car2);
			
			System.out.println("Saved:");
			Query query = container.query();
			query.constrain(Car.class);
			ObjectSet results = query.execute();
			listResult(results);
		} finally {
			container.close();
		}
	}
	// end setCars
	
	private static void getCars()
	{
		ObjectContainer container=Db4o.openFile(DB4O_FILE_NAME);
		try {
			GenericReflector reflector = new GenericReflector(null,container.ext().reflector());
			ReflectClass carClass = reflector.forName(Car.class.getName());
			System.out.println("Reflected class "+carClass);
			System.out.println("Retrieved with reflector:");
			Query query = container.query();
			query.constrain(carClass);
			ObjectSet results = query.execute();
			listResult(results);
		} finally {
			container.close();
		}
	}
	// end getCars
	
	private static void getCarInfo()
	{
		ObjectContainer container=Db4o.openFile(DB4O_FILE_NAME);
		try {
			GenericReflector reflector = new GenericReflector(null,container.ext().reflector());
			ReflectClass carClass = reflector.forName(Car.class.getName());
			System.out.println("Reflected class "+carClass);
			 // public fields
			System.out.println("FIELDS:");
			ReflectField[] fields = carClass.getDeclaredFields();
			for (int i = 0; i < fields.length; i++)
				System.out.println(fields[i].getName());
			
			// constructors
			System.out.println("CONSTRUCTORS:");
			ReflectConstructor[] cons = carClass.getDeclaredConstructors();
			for (int i = 0; i < cons.length; i++)
				System.out.println( cons[i]);
			
			// public methods
			System.out.println("METHODS:");
			ReflectMethod method = carClass.getMethod("getPilot",null);
			System.out.println(method.getClass());

		} finally {
			container.close();
		}
	}
	// end getCarInfo
	
	private static void getReflectorInfo()
	{
		ObjectContainer container=Db4o.openFile(DB4O_FILE_NAME);
		try {
			System.out.println("Reflector in use: " + container.ext().reflector());
			System.out.println("Reflector delegate" +container.ext().reflector().getDelegate());
			ReflectClass[] knownClasses = container.ext().reflector().knownClasses();
			int count = knownClasses.length;
			System.out.println("Known classes: " + count);
			for (int i=0; i <knownClasses.length; i++){
				System.out.println(knownClasses[i]);
			}
		} finally {
			container.close();
		}
	}
	// end getReflectorInfo
	
	private static void testReflector()
	{
		LoggingReflector logger = new LoggingReflector();
		Configuration configuration = Db4o.newConfiguration();
		configuration.reflectWith(logger);
		ObjectServer server = Db4o.openServer( DB4O_FILE_NAME, 0xdb40);
		server.grantAccess("y", "y");
		ObjectContainer container = server.openClient();
		//ObjectContainer container = Db4o.openClient(configuration, "localhost", 0xdb40,"y", "y");
		//ObjectContainer container=Db4o.openFile(configuration , DB4O_FILE_NAME);
		try {
			ReflectClass rc  = container.ext().reflector().forName(Car.class.getName());
			System.out.println("Reflected class: " + rc);
		} finally {
			container.close();
		}
	}
	// end testReflector
	
	private static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
