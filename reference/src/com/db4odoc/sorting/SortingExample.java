/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.sorting;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Candidate;
import com.db4o.query.Evaluation;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import com.db4o.query.QueryComparator;


public class SortingExample {
	public final static String YAPFILENAME="formula1.yap";
	
	public static void main(String[] args) {
		Db4o.configure().objectClass(Pilot.class).objectField("name").indexed(true);
		Db4o.configure().objectClass(Pilot.class).objectField("points").indexed(true);
		setObjects();
		getObjectsNQ();
		getObjectsSODA();
		getObjectsEval();
	}
	// end main

	public static void setObjects(){
		new File(YAPFILENAME).delete();
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			for (int i = 0; i< 10; i++){
				for (int j = 0; j < 5; j++){
					Pilot pilot = new Pilot("Pilot #"+i, j+1);
					db.set(pilot);
				}
			}
		} finally {
			db.close();
		}
	}
	// end setObjects

	public static void getObjectsEval(){
		
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			long t1 = System.currentTimeMillis();
			Query query = db.query();
			query.constrain(Pilot.class);
			query.constrain(new Evaluation(){
				public void evaluate(Candidate candidate){
					Pilot pilot = (Pilot)candidate.getObject();
					candidate.include(pilot.getPoints() % 2 == 0);
				}
			});
			List <Pilot>result= new ArrayList<Pilot>(query.execute());
			Collections.sort(result, new Comparator<Pilot>(){
				   public int compare(Pilot p1, Pilot p2) {
				     return p1.getName().compareTo(p2.getName());
				   }   
				  });
			long t2 = System.currentTimeMillis();
			long diff = t2 - t1;
			System.out.println("Time to execute with Evaluation query and collection sorting: " + diff + " ms.");
			listResult(result);
		} finally {
			db.close();
		}
	}
	// end getObjectsEval

	public static void getObjectsSODA(){
		
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			Query query = db.query();
			query.constrain(Pilot.class);
			query.descend("name").orderAscending();
			query.descend("points").orderDescending();
			long t1 = System.currentTimeMillis();
			ObjectSet result= query.execute();
			long t2 = System.currentTimeMillis();
			long diff = t2 - t1;
			System.out.println("Time to query and sort with  SODA: " + diff + " ms.");
			listResult(result);
		} finally {
			db.close();
		}
	}
	// end getObjectsSODA
	
	public static void getObjectsNQ(){
		ObjectContainer db = Db4o.openFile(YAPFILENAME);
		try {
			long t1 = System.currentTimeMillis();
			ObjectSet result = db.query(new  Predicate<Pilot>(){
	    	    public boolean match(Pilot pilot) {
	    	        return true;
	    	    }
			}, new QueryComparator<Pilot>() {
	            public int compare(Pilot p1, Pilot p2)
	            {
	            	int result = p1.getPoints() - p2.getPoints();
	            	if (result == 0){
	            		return p1.getName().compareTo(p2.getName());
	            	} else {
	            		return -result;
	            	}
	            }
	        });
			long t2 = System.currentTimeMillis();
			long diff = t2 - t1;
			System.out.println("Time to execute with NQ and comparator: " + diff + " ms.");
			listResult(result);
		} finally {
			db.close();
		}
	}
	// end getObjectsNQ

	public static void listResult(List result) {
        System.out.println(result.size());
        for (int i = 0; i < result.size(); i++){
            System.out.println(result.get(i));
        }
    }
    // end listResult
	
	public static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
