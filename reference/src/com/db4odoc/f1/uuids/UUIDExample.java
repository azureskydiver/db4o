/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.uuids;

import java.io.File;
import java.util.Date;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.YapFile;
import com.db4o.ext.Db4oDatabase;
import com.db4o.ext.Db4oUUID;
import com.db4o.ext.ObjectInfo;
import com.db4o.foundation.TimeStampIdGenerator;
import com.db4o.query.Query;
import com.db4odoc.f1.Util;


public class UUIDExample extends Util {


	public static void main(String[] args) {
		testChangeIdentity();
		setObjects();
		testGenerateUUID();
	}
	
	private static String printSignature(byte[] signature){
		String str = "";
		 for (int i = 0; i < signature.length; i++) {
	            str = str + signature[i];
	     }
	     return str;
	} 
	
	public static void testChangeIdentity(){
		new File(Util.YAPFILENAME).delete();
		ObjectContainer oc = Db4o.openFile(Util.YAPFILENAME);
		Db4oDatabase db;
		byte[] oldSignature;
		byte[] newSignature;
        try {
	        db = oc.ext().identity();
	        oldSignature = db.getSignature();
	        System.out.println("oldSignature: " + printSignature(oldSignature));	
	        ((YapFile)oc).generateNewIdentity();
        } finally {
        	oc.close();
        }        
        oc = Db4o.openFile(Util.YAPFILENAME);
        try {
	        db = oc.ext().identity();
	        newSignature = db.getSignature();
	        System.out.println("newSignature: " + printSignature(newSignature));
        } finally {
        	oc.close();
        }
        
        boolean same = true;
        
        for (int i = 0; i < oldSignature.length; i++) {
            if(oldSignature[i] != newSignature[i]){
                same =false;
            }
        }
        
        if (same){
        	System.out.println("Database signatures are identical");
        } else {
        	System.out.println("Database signatures are different");
        }
	}

	public static void setObjects(){
		Db4o.configure().objectClass(Pilot.class).generateUUIDs(true);
		new File(Util.YAPFILENAME).delete();
		ObjectContainer oc = Db4o.openFile(Util.YAPFILENAME);
		try {
			Car car = new Car("BMW", new Pilot("Rubens Barrichello"));
			oc.set(car);
		} finally {
			oc.close();
		}
	}
	public static void testGenerateUUID(){
		ObjectContainer oc = Db4o.openFile(Util.YAPFILENAME);
		try {
			Query query = oc.query();
			query.constrain(Car.class);
			ObjectSet result = query.execute();
			Car car = (Car)result.get(0);
			ObjectInfo carInfo = oc.ext().getObjectInfo(car);
			Db4oUUID carUUID = carInfo.getUUID();
			System.out.println("UUID for Car class are not generated:");
			System.out.println("Car UUID: " + carUUID);
			
			Pilot pilot = car.getPilot();
			ObjectInfo pilotInfo = oc.ext().getObjectInfo(pilot);
			Db4oUUID pilotUUID = pilotInfo.getUUID();
			System.out.println("UUID for Pilot:");
			System.out.println("Pilot UUID: " + pilotUUID);
			System.out.println("long part: " + pilotUUID.getLongPart() +"; signature: " + printSignature(pilotUUID.getSignaturePart()));
			long ms = TimeStampIdGenerator.idToMilliseconds(pilotUUID.getLongPart());
			System.out.println("Pilot object was created: " + (new Date(ms)).toString());
			Pilot pilotReturned = (Pilot) oc.ext().getByUUID(pilotUUID);
			System.out.println("Pilot from UUID: " + pilotReturned);	
        } finally {
        	oc.close();
        }        
	}
}
