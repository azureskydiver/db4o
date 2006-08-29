/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.activating;

import java.io.File;
import java.util.List;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4odoc.f1.Util;
import com.db4o.P2LinkedList;



public class ActivationExample extends Util {
	
	public static void main(String[] args){
		testActivationDefault();
		testActivationConfig();
		testCascadeActivate();
		testMaxActivate();
		testMinActivate();
		testActivateDeactivate();
		testCollectionDef();
		testCollectionActivation();
	}
	
	public static void storeSensorPanel(){
		new File(Util.YAPFILENAME).delete();
		ObjectContainer db = Db4o.openFile(Util.YAPFILENAME);
		try {
			// create a linked list with length 10
			SensorPanel list = new SensorPanel().createList(10); 
			// store all elements with one statement, since all elements are new		
			db.set(list);
		} finally {
			db.close();
		}
	}
	
	public static void testActivationConfig(){
		storeSensorPanel();
		ObjectContainer db = Db4o.openFile(Util.YAPFILENAME);
		try {
			db.ext().configure().activationDepth(1);
			System.out.println("Object container activation depth = 1");
			ObjectSet result = db.get(new SensorPanel(1));
			listResult(result);
			if (result.size() >0) {
				SensorPanel sensor = (SensorPanel)result.get(0);
				SensorPanel next = sensor.next;
				while (next != null){
					System.out.println(next);
					next = next.next;
				}
			}
		} finally {
			db.close();
		}
	}

	public static void testActivationDefault(){
		storeSensorPanel();
		ObjectContainer db = Db4o.openFile(Util.YAPFILENAME);
		try {
			System.out.println("Default activation depth");
			ObjectSet result = db.get(new SensorPanel(1));
			listResult(result);
			if (result.size() >0) {
				SensorPanel sensor = (SensorPanel)result.get(0);
				SensorPanel next = sensor.next;
				while (next != null){
					System.out.println(next);
					next = next.next;
				}
			}
		} finally {
			db.close();
		}
	}
	
	public static void testCascadeActivate(){
		storeSensorPanel();
		ObjectContainer db = Db4o.openFile(Util.YAPFILENAME);
		db.ext().configure().objectClass(SensorPanel.class).cascadeOnActivate(true);
		try {
			System.out.println("Cascade activation");
			ObjectSet result = db.get(new SensorPanel(1));
			listResult(result);
			if (result.size() >0) {
				SensorPanel sensor = (SensorPanel)result.get(0);
				SensorPanel next = sensor.next;
				while (next != null){
					System.out.println(next);
					next = next.next;
				}
			}
		} finally {
			db.close();
		}
	}
	
	public static void testMinActivate(){
		storeSensorPanel();
		// note that the minimum applies for *all* instances in the hierarchy
		// the system ensures that every instantiated List object will have it's 
		// members set to a depth of 1
		Db4o.configure().objectClass(SensorPanel.class).minimumActivationDepth(1);
		ObjectContainer db = Db4o.openFile(Util.YAPFILENAME);
		try {
			System.out.println("Minimum activation depth = 1");
			ObjectSet result = db.get(new SensorPanel(1));
			listResult(result);
			if (result.size() >0) {
				SensorPanel sensor = (SensorPanel)result.get(0);
				SensorPanel next = sensor.next;
				while (next != null){
					System.out.println(next);
					next = next.next;
				}
			}
		} finally {
			db.close();
			Db4o.configure().objectClass(SensorPanel.class).minimumActivationDepth(0);
		}
	}
		
	public static void testMaxActivate() {
		storeSensorPanel();
		// note that the maximum is applied to the retrieved root object and limits activation
		// further down the hierarchy
		Db4o.configure().objectClass(SensorPanel.class).maximumActivationDepth(2);

		ObjectContainer db = Db4o.openFile(Util.YAPFILENAME);
		try {
			System.out.println("Maximum activation depth = 2 (default = 5)");
			ObjectSet result = db.get(new SensorPanel(1));
			listResult(result);
			if (result.size() > 0) {
				SensorPanel sensor = (SensorPanel) result.get(0);
				SensorPanel next = sensor.next;
				while (next != null) {
					System.out.println(next);
					next = next.next;
				}
			}
		} finally {
			db.close();
			Db4o.configure().objectClass(SensorPanel.class).maximumActivationDepth(Integer.MAX_VALUE);
		}
		
	}
	
	public static void testActivateDeactivate(){
		storeSensorPanel();
		ObjectContainer db = Db4o.openFile(Util.YAPFILENAME);
		db.ext().configure().activationDepth(0);
		try {
			System.out.println("Object container activation depth = 0" );
			ObjectSet result = db.get(new SensorPanel(1));
			System.out.println("Sensor1:");
			listResult(result);
			SensorPanel sensor1 = (SensorPanel)result.get(0);
			testActivated(sensor1);
			
			System.out.println("Sensor1 activated:");
			db.activate(sensor1,4);
			testActivated(sensor1);
			
			System.out.println("Sensor5 activated:");
			result = db.get(new SensorPanel(5));
			SensorPanel sensor5 = (SensorPanel)result.get(0);
			db.activate(sensor5,4);
			listResult(result);
			testActivated(sensor5);
			
			System.out.println("Sensor1 deactivated:");
			db.deactivate(sensor1,5);
			testActivated(sensor1);
			
			//			 	DANGER !!!.
			// If you use deactivate with a higher value than 1
			// make sure that you know whereto members might branch
			// Deactivating list1 also deactivated list5
			System.out.println("Sensor 5 AFTER DEACTIVATE OF Sensor1.");
			testActivated(sensor5);
		} finally {
			db.close();
		}
	}
	
	public static void testActivated(SensorPanel sensor){
		SensorPanel next = sensor;
		do {
			next = next.next;
			System.out.println(next);
		} while (next != null);
	}
	
	public static void storeCollection(){
		new File(Util.YAPFILENAME).delete();
		ObjectContainer db = Db4o.openFile(Util.YAPFILENAME);
		try {
			List list = db.ext().collections().newLinkedList(); 
			for (int i =0; i < 10; i++){
				SensorPanel sensor = new SensorPanel(i);
				list.add(sensor);
			}		
			db.set(list);
		} finally {
			db.close();
		}
	}
	
	public static void testCollectionDef(){
		storeCollection();
		ObjectContainer db = Db4o.openFile(Util.YAPFILENAME);
		db.ext().configure().activationDepth(5);
		try {
			ObjectSet result = db.get(List.class);
			listResult(result);
			P2LinkedList list = (P2LinkedList)result.get(0);
			System.out.println("Default List activation depth: " + list.activationDepth());
			for (int i = 0; i < list.size(); i++){
				System.out.println("List element: " + list.get(i));
			}
		} finally {
			db.close();
		} 
	}
	
	public static void testCollectionActivation(){
		storeCollection();
		ObjectContainer db = Db4o.openFile(Util.YAPFILENAME);
		db.ext().configure().activationDepth(5);
		try {
			ObjectSet result = db.get(List.class);
			listResult(result);
			P2LinkedList list = (P2LinkedList)result.get(0);
			System.out.println("Setting list activation depth to 0 ");
			list.activationDepth(0);
			for (int i = 0; i < list.size(); i++){
				System.out.println("List element: " + list.get(i));
			}
		} finally {
			db.close();
		} 
	}

}
