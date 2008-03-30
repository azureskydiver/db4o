package com.db4o.f1.chapter1;

import java.util.*;

import com.db4o.*;
import com.db4o.f1.*;
import com.db4o.query.*;

public class NQExample extends Util {
	
    public static void main(String[] args) {
        ObjectContainer db=Db4o.openFile(Util.DB4OFILENAME);
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

    public static void storePilots(ObjectContainer db) {
        db.store(new Pilot("Michael Schumacher",100));
        db.store(new Pilot("Rubens Barrichello",99));
    }

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
    
    public static void retrieveComplexNQ(ObjectContainer db) {
        List<Pilot> result=db.query(new Predicate<Pilot>() {
        	public boolean match(Pilot pilot) {
        		return pilot.getPoints()>99
        			&& pilot.getPoints()<199
        			|| pilot.getName().equals("Rubens Barrichello");
			}
        });
        listResult(result);
    }

    public static void retrieveArbitraryCodeNQ(ObjectContainer db) {
    	final int[] points={1,100};
        List<Pilot> result=db.query(new Predicate<Pilot>() {
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

    public static void clearDatabase(ObjectContainer db) {
        ObjectSet result=db.queryByExample(Pilot.class);
        while(result.hasNext()) {
            db.delete(result.next());
        }
    }
}
