/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.lists;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;
import java.util.List;


public class CollectionExample {
	public final static String YAPFILENAME="formula1.yap";

	public static void main(String[] args) {
		setTeam();
		updateTeam();
	}
	// end main
	
	public static void setTeam(){
		 new File(YAPFILENAME).delete();
		  ObjectContainer db=Db4o.openFile(YAPFILENAME);
		   try {
			   Team ferrariTeam = new Team();
			   ferrariTeam.setName("Ferrari");
			   
			   Pilot pilot1 = new Pilot("Michael Schumacher", 100);
			   ferrariTeam.addPilot(pilot1);
			   Pilot pilot2 = new Pilot("David Schumacher", 98);
			   ferrariTeam.addPilot(pilot2);
				
			   db.set(ferrariTeam);
			   List protoList = CollectionFactory.newList();
			   ObjectSet result = db.get(protoList);
			   listResult(result);
		   }  finally {
		      db.close();
		    } 
	}
	// end setTeam

	public static void updateTeam(){
		ObjectContainer db=Db4o.openFile(YAPFILENAME);
		 try {
			 Query query =db.query(); 
			 query.constrain(Team.class);
			 query.descend("name").constrain("Ferrari");
			 ObjectSet result = query.execute();
			 if (result.hasNext()) {
				 Team ferrariTeam = (Team)result.next();
					
				 Pilot pilot = new Pilot("David Schumacher", 100);
				 ferrariTeam.updatePilot(1,pilot);
					
				 db.set(ferrariTeam);
			 }
			 List protoList = CollectionFactory.newList();
			 result = db.get(protoList);
			 listResult(result);
		}  finally {
			db.close();
		} 
	}
	// end updateTeam
	
    public static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
