/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.utility;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4odoc.f1.activating.SensorPanel;


public class UtilityExample {
	public final static String YAPFILENAME="formula1.yap";

	public static void main(String[] args) {
		testDescend();
		checkActive();
		checkStored();
	}
	// end main

	public static void storeSensorPanel(){
		new File(YAPFILENAME).delete();
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			// create a linked list with length 10
			SensorPanel list = new SensorPanel().createList(10); 
			// store all elements with one statement, since all elements are new		
			db.set(list);
		} finally {
			db.close();
		}
	}
	// end storeSensorPanel
	
	public static void testDescend(){
		storeSensorPanel();
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			db.ext().configure().activationDepth(1);
			System.out.println("Object container activation depth = 1");
			ObjectSet result = db.get(new SensorPanel(1));
			SensorPanel spParent = (SensorPanel)result.get(0);
			SensorPanel spDescend = (SensorPanel)db.ext().descend((Object)spParent, new String[]{"next","next","next","next","next"});
			db.ext().activate(spDescend, 5);
			System.out.println(spDescend);
		} finally {
			db.close();
		}
	}
	// end testDescend
	
	public static void checkActive(){
		storeSensorPanel();
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			db.ext().configure().activationDepth(2);
			System.out.println("Object container activation depth = 2");
			ObjectSet result = db.get(new SensorPanel(1));
			SensorPanel sensor = (SensorPanel)result.get(0); 
			SensorPanel next = sensor.next;
			while (next != null){
				System.out.println("Object " + next +" is active: " + db.ext().isActive(next));
				next = next.next;
			}
		} finally {
			db.close();
		}
	}
	// end checkActive
	
	public static void checkStored(){
		// create a linked list with length 10
		SensorPanel list = new SensorPanel().createList(10);
		new File(YAPFILENAME).delete();
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			// store all elements with one statement, since all elements are new		
			db.set(list);
			Object sensor = (Object)list.sensor;
			SensorPanel sp5 = list.next.next.next.next;
			System.out.println("Root element "+list+" isStored: " + db.ext().isStored(list));
			System.out.println("Simple type  "+sensor+" isStored: " + db.ext().isStored(sensor));
			System.out.println("Descend element  "+sp5+" isStored: " + db.ext().isStored(sp5));
			db.delete(list);
			System.out.println("Root element "+list+" isStored: " + db.ext().isStored(list));
		} finally {
			db.close();
		}
	}
	// end checkStored
}
