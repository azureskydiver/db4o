/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.reflections;


import java.io.File;
import java.io.IOException;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectConstructor;
import com.db4o.reflect.ReflectField;
import com.db4o.reflect.ReflectMethod;
import com.db4o.reflect.generic.GenericReflector;
import com.db4odoc.f1.Util;
import com.db4odoc.f1.evaluations.Car;

public class ReflectorExample extends Util {
	
	public static void main(String[] args) throws IOException {
		setCars();
		getReflectorInfo();
		getCars();
		getCarInfo();
	}
	
	public static void setCars()
	{
		 new File(Util.YAPFILENAME).delete();
		 ObjectContainer db=Db4o.openFile(Util.YAPFILENAME);
		try {
			Car car1 = new Car("BMW");
			db.set(car1);
			Car car2 = new Car("Ferrari");
			db.set(car2);
			
			System.out.println("Saved:");
			Query query = db.query();
			query.constrain(Car.class);
			ObjectSet results = query.execute();
			listResult(results);
		} finally {
			db.close();
		}
	}
	
	public static void getCars()
	{
		ObjectContainer db=Db4o.openFile(Util.YAPFILENAME);
		try {
			GenericReflector reflector = new GenericReflector(null,db.ext().reflector());
			ReflectClass carClass = reflector.forName(Car.class.getName());
			System.out.println("Reflected class "+carClass);
			System.out.println("Retrieved with reflector:");
			Query query = db.query();
			query.constrain(carClass);
			ObjectSet results = query.execute();
			listResult(results);
		} finally {
			db.close();
		}
	}
	
	public static void getCarInfo()
	{
		ObjectContainer db=Db4o.openFile(Util.YAPFILENAME);
		try {
			GenericReflector reflector = new GenericReflector(null,db.ext().reflector());
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
			db.close();
		}
	}
	public static void getReflectorInfo()
	{
		ObjectContainer db=Db4o.openFile(Util.YAPFILENAME);
		try {
			System.out.println("Reflector in use: " + db.ext().reflector());
			System.out.println("Reflector delegate" +db.ext().reflector().getDelegate());
			ReflectClass[] knownClasses = db.ext().reflector().knownClasses();
			int count = knownClasses.length;
			System.out.println("Known classes: " + count);
			for (int i=0; i <knownClasses.length; i++){
				System.out.println(knownClasses[i]);
			}
		} finally {
			db.close();
		}
	}
	
	public static void testReflector()
	{
		LoggingReflector logger = new LoggingReflector();
		Db4o.configure().reflectWith(logger);
		ObjectContainer db=Db4o.openFile(Util.YAPFILENAME);
		try {
			ReflectClass rc  = db.ext().reflector().forName(Car.class.getName());
			System.out.println("Reflected class: " + rc);
		} finally {
			db.close();
		}
	}
}
