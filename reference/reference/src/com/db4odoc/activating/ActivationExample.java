/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.activating;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.config.*;



public class ActivationExample {
	private final static String DB4O_FILE_NAME="reference.db4o";
	 
	public static void main(String[] args){
		//testActivationDefault();
		//testActivationConfig();
//		testCascadeActivate();
//		testMaxActivate();
		testMinActivate();
//		testActivateDeactivate();
//		testCollectionDef();
//		testCollectionActivation();
	}
	//	end main
	
	private  static void storeSensorPanel(){
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			// create a linked list with length 10
			SensorPanel list = new SensorPanel().createList(10); 
			// store all elements with one statement, since all elements are new		
			container.store(list);
		} finally {
			container.close();
		}
	}
	// end storeSensorPanel
	
	private static void testActivationConfig(){
		storeSensorPanel();
		Configuration configuration = Db4o.newConfiguration();
		configuration.activationDepth(1);
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			System.out.println("Object container activation depth = 1");
			ObjectSet result = container.queryByExample(new SensorPanel(1));
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
			container.close();
		}
	}
	// end testActivationConfig

	private static void testActivationDefault(){
		storeSensorPanel();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			System.out.println("Default activation depth");
			ObjectSet result = container.queryByExample(new SensorPanel(1));
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
			container.close();
		}
	}
	// end testActivationDefault
	
	private static void testCascadeActivate(){
		storeSensorPanel();
		Configuration configuration = Db4o.newConfiguration();
		configuration.objectClass(SensorPanel.class).cascadeOnActivate(true);
		configuration.activationDepth(0);
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			System.out.println("Cascade activation");
			ObjectSet result = container.queryByExample(new SensorPanel(1));
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
			container.close();
		}
	}
	// end testCascadeActivate
	
	private static void testMinActivate(){
		storeSensorPanel();
		// note that the minimum applies for *all* instances in the hierarchy
		// the system ensures that every instantiated List object will have it's 
		// members set to a depth of 1
		Configuration configuration = Db4o.newConfiguration();
		configuration.objectClass(SensorPanel.class).minimumActivationDepth(1);
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			System.out.println("Minimum activation depth = 1");
			ObjectSet result = container.queryByExample(new SensorPanel(1));
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
			container.close();
		}
	}
	// end testMinActivate
		
	private static void testMaxActivate() {
		storeSensorPanel();
		// note that the maximum is applied to the retrieved root object and limits activation
		// further down the hierarchy
		Configuration configuration = Db4o.newConfiguration();
		configuration.objectClass(SensorPanel.class).maximumActivationDepth(2);

		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			System.out.println("Maximum activation depth = 2 (default = 5)");
			ObjectSet result = container.queryByExample(new SensorPanel(1));
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
			container.close();
		}
	}
	// end testMaxActivate
	
	private static void testActivateDeactivate(){
		storeSensorPanel();
		Configuration configuration = Db4o.newConfiguration();
		configuration.activationDepth(0);
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			System.out.println("Object container activation depth = 0" );
			ObjectSet result = container.queryByExample(new SensorPanel(1));
			System.out.println("Sensor1:");
			listResult(result);
			SensorPanel sensor1 = (SensorPanel)result.get(0);
			testActivated(sensor1);
			
			System.out.println("Sensor1 activated:");
			container.activate(sensor1,4);
			testActivated(sensor1);
			
			System.out.println("Sensor5 activated:");
			result = container.queryByExample(new SensorPanel(5));
			SensorPanel sensor5 = (SensorPanel)result.get(0);
			container.activate(sensor5,4);
			listResult(result);
			testActivated(sensor5);
			
			System.out.println("Sensor1 deactivated:");
			container.deactivate(sensor1,5);
			testActivated(sensor1);
			
			//			 	DANGER !!!.
			// If you use deactivate with a higher value than 1
			// make sure that you know whereto members might branch
			// Deactivating list1 also deactivated list5
			System.out.println("Sensor 5 AFTER DEACTIVATE OF Sensor1.");
			testActivated(sensor5);
		} finally {
			container.close();
		}
	}
	// end testActivateDeactivate
	
	private static void testActivated(SensorPanel sensor){
		SensorPanel next = sensor;
		do {
			next = next.next;
			System.out.println(next);
		} while (next != null);
	}
	// end testActivated
	
	private static void storeCollection(){
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			List list = (List)container.ext().collections().newLinkedList(); 
			for (int i =0; i < 10; i++){
				SensorPanel sensor = new SensorPanel(i);
				list.add(sensor);
			}		
			container.store(list);
		} finally {
			container.close();
		}
	}
	// end storeCollection
	
	private static void testCollectionDef(){
		storeCollection();
		Configuration configuration = Db4o.newConfiguration();
		configuration.activationDepth(5);
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try {
			ObjectSet result = container.queryByExample(List.class);
			listResult(result);
			P2LinkedList list = (P2LinkedList)result.get(0);
			//System.out.println("Default List activation depth: " + list.activationDepth());
			for (int i = 0; i < list.size(); i++){
				System.out.println("List element: " + list.get(i));
			}
		} finally {
			container.close();
		} 
	}
	// end testCollectionDef
	
	private static void testCollectionActivation(){
		storeCollection();
		Configuration configuration = Db4o.newConfiguration();
		configuration.activationDepth(5);
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			ObjectSet result = container.queryByExample(List.class);
			listResult(result);
			P2LinkedList list = (P2LinkedList)result.get(0);
			System.out.println("Setting list activation depth to 0 ");
			list.activationDepth(0);
			for (int i = 0; i < list.size(); i++){
				System.out.println("List element: " + list.get(i));
			}
		} finally {
			container.close();
		} 
	}
	// end testCollectionActivation
	
	private static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult

}
