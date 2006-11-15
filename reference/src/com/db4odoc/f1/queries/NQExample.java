package com.db4odoc.f1.queries;

import java.util.List;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.db4o.query.Query;

public class NQExample {
	public final static String YAPFILENAME="formula1.yap";
	
    public static void main(String[] args) {
        ObjectContainer db=Db4o.openFile(YAPFILENAME);
        try {
            storePilots(db);
            retrieveComplexSODA(db);
            retrieveComplexNQ(db);
            retrieveArbitraryCodeNQ(db);
            clearDatabase(db);
        }
        finally {
            db.close();
        }
    }
    // end main

    public static void primitiveQuery(ObjectContainer db){
    	List <Pilot> pilots = db.query(new Predicate<Pilot>() {
    	    public boolean match(Pilot pilot) {
    	        return pilot.getPoints() == 100;
    	    }
    	});	
    }
    // end primitiveQuery
    
    
    public static void advancedQuery(ObjectContainer db){
    	List <Pilot> result = db.query(new Predicate<Pilot>() {
    	    public boolean match(Pilot pilot) {
    	        return pilot.getPoints() > 99
    	            && pilot.getPoints() < 199
    	            || pilot.getName().equals("Rubens Barrichello");
    	   }
    	});	
    }
    // end advancedQuery
    
    public static void storePilots(ObjectContainer db) {
        db.set(new Pilot("Michael Schumacher",100));
        db.set(new Pilot("Rubens Barrichello",99));
    }
    // end storePilots

    public static void retrieveComplexSODA(ObjectContainer db) {
        Query query=db.query();
        query.constrain(Pilot.class);
        Query pointQuery=query.descend("points");
        query.descend("name").constrain("Rubens Barrichello")
        	.or(pointQuery.constrain(new Integer(99)).greater()
        	    .and(pointQuery.constrain(new Integer(199)).smaller()));
        ObjectSet result=query.execute();
        listResult(result);
    }
    // end retrieveComplexSODA
    
    public static void retrieveComplexNQ(ObjectContainer db) {
        ObjectSet result=db.query(new Predicate<Pilot>() {
        	public boolean match(Pilot pilot) {
        		return pilot.getPoints()>99
        			&& pilot.getPoints()<199
        			|| pilot.getName().equals("Rubens Barrichello");
			}
        });
        listResult(result);
    }
    // end retrieveComplexNQ

    public static void retrieveArbitraryCodeNQ(ObjectContainer db) {
    	final int[] points={1,100};
        ObjectSet result=db.query(new Predicate<Pilot>() {
        	public boolean match(Pilot pilot) {
        		for(int i=0;i<points.length;i++) {
        			if(pilot.getPoints()==points[i]) {
        				return true;
        			}
        		}
        		return pilot.getName().startsWith("Rubens");
			}
        });
        listResult(result);
    }
    // end retrieveArbitraryCodeNQ

    public static void clearDatabase(ObjectContainer db) {
        ObjectSet result=db.get(Pilot.class);
        while(result.hasNext()) {
            db.delete(result.next());
        }
    }
    // end clearDatabase
    
    public static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
}
