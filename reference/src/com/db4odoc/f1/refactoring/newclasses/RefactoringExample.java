/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.refactoring.newclasses;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.ext.StoredClass;
import com.db4o.ext.StoredField;
import com.db4o.query.Query;
import com.db4odoc.f1.Util;


public class RefactoringExample extends Util {

	public static void main(String[] args) {

	}

	public static void reopenDB(){
		ObjectContainer oc = Db4o.openFile(Util.YAPFILENAME);
		oc.close();
	}
	
	public static void transferValues(){
		ObjectContainer oc = Db4o.openFile(Util.YAPFILENAME);
		try {
			StoredClass sc = oc.ext().storedClass("com.db4odoc.f1.refactoring.oldclasses.Pilot");
			System.out.println("Stored class:  "+ sc.toString());
			StoredField sfOld = sc.storedField("name",String.class);
			System.out.println("Old field:  "+ sfOld.toString()+";"+sfOld.getStoredType());
			Query q = oc.query();
			q.constrain(Pilot.class);
			ObjectSet result = q.execute();
			for (int i = 0; i< result.size(); i++){
				Pilot pilot = (Pilot)result.get(i);
				System.out.println("Pilot="+ pilot);
				pilot.setName(new Identity(sfOld.get(pilot).toString(),""));
				System.out.println("Pilot="+ pilot);
				oc.set(pilot);
			}
			
		} finally {
			oc.close();
		}
	}

}
