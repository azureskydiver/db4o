/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.metainf;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ext.StoredClass;
import com.db4o.ext.StoredField;

public class MetaInfExample {
	public final static String YAPFILENAME="formula1.yap";
	public static void main(String[] args) {
		setObjects();
		getMetaObjects();
		getMetaObjectsInfo();
	}
	// end main

	public static void setObjects(){
		new File(YAPFILENAME).delete();
		ObjectContainer oc = Db4o.openFile(YAPFILENAME);
		try {
			Car car = new Car("BMW", new Pilot("Rubens Barrichello"));
			oc.set(car);
			car = new Car("Ferrari", new Pilot("Michael Schumacher"));
			oc.set(car);
		} finally {
			oc.close();
		}
	}
	// end setObjects
	
	public static void getMetaObjects(){
		ObjectContainer oc = Db4o.openFile(YAPFILENAME);
		try {
			System.out.println("Retrieve meta information for class: ");
			StoredClass sc = oc.ext().storedClass(Car.class.getName());
			System.out.println("Stored class:  "+ sc.toString());
			
			System.out.println("Retrieve meta information for all classes in database: ");
			StoredClass sclasses[] = oc.ext().storedClasses();
			for (int i=0; i< sclasses.length; i++){
				System.out.println(sclasses[i].getName());	
			}
		} finally {
			oc.close();
		}
	}
	// end getMetaObjects
	
	public static void getMetaObjectsInfo(){
		ObjectContainer oc = Db4o.openFile(YAPFILENAME);
		try {
			System.out.println("Retrieve meta information for field: ");
			StoredClass sc = oc.ext().storedClass(Car.class.getName());
			StoredField sf = sc.storedField("pilot",Pilot.class);
			System.out.println("Field info:  "+ sf.getName()+"/"+sf.getStoredType()+"/isArray="+sf.isArray());
			
			System.out.println("Retrieve all fields: ");
			StoredField sfields[] = sc.getStoredFields();
			for (int i=0; i< sfields.length; i++){
				System.out.println("Stored field:  "+ sfields[i].getName()+"/"+sfields[i].getStoredType());
			}
		} finally {
			oc.close();
		}
	}
	// end getMetaObjectsInfo
}
