/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.lists;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.query.*;

public class CollectionExample {
	private final static String DB4O_FILE_NAME="reference.db4o";

	public static void main(String[] args) {
		setTeam();
		updateTeam();
	}
	// end main
	
	private static void setTeam(){
		 new File(DB4O_FILE_NAME).delete();
		  ObjectContainer container=Db4o.openFile(DB4O_FILE_NAME);
		   try {
			   Team ferrariTeam = new Team();
			   ferrariTeam.setName("Ferrari");
			   
			   Pilot pilot1 = new Pilot("Michael Schumacher", 100);
			   ferrariTeam.addPilot(pilot1);
			   Pilot pilot2 = new Pilot("David Schumacher", 98);
			   ferrariTeam.addPilot(pilot2);
				
			   container.store(ferrariTeam);
			   List protoList = CollectionFactory.newList();
			   ObjectSet result = container.queryByExample(protoList);
			   listResult(result);
		   }  finally {
		      container.close();
		    } 
	}
	// end setTeam

	private static void updateTeam(){
		ObjectContainer container=Db4o.openFile(DB4O_FILE_NAME);
		 try {
			 Query query =container.query(); 
			 query.constrain(Team.class);
			 query.descend("name").constrain("Ferrari");
			 ObjectSet result = query.execute();
			 if (result.hasNext()) {
				 Team ferrariTeam = (Team)result.next();
					
				 Pilot pilot = new Pilot("David Schumacher", 100);
				 ferrariTeam.updatePilot(1,pilot);
					
				 container.store(ferrariTeam);
			 }
			 List protoList = CollectionFactory.newList();
			 result = container.queryByExample(protoList);
			 listResult(result);
		}  finally {
			container.close();
		} 
	}
	// end updateTeam
	
	private static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
