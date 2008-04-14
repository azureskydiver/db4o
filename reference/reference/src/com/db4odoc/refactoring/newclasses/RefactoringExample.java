/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.refactoring.newclasses;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;


public class RefactoringExample {

	private final static String DB4O_FILE_NAME="reference.db4o";
	
	public static void main(String[] args) {
		reopenDB();
		transferValues();
	}
	// end main

	private static void reopenDB(){
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		container.close();
	}
	// end reopenDB
	
	private static void transferValues(){
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			StoredClass sc = container.ext().storedClass("com.db4odoc.f1.refactoring.oldclasses.Pilot");
			System.out.println("Stored class:  "+ sc.toString());
			StoredField sfOld = sc.storedField("name",String.class);
			System.out.println("Old field:  "+ sfOld.toString()+";"+sfOld.getStoredType());
			Query q = container.query();
			q.constrain(Pilot.class);
			ObjectSet result = q.execute();
			for (int i = 0; i< result.size(); i++){
				Pilot pilot = (Pilot)result.get(i);
				System.out.println("Pilot="+ pilot);
				pilot.setName(new Identity(sfOld.get(pilot).toString(),""));
				System.out.println("Pilot="+ pilot);
				container.store(pilot);
			}
			
		} finally {
			container.close();
		}
	}
	// end transferValues
}
