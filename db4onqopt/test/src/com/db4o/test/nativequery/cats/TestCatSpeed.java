/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.nativequery.cats;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;
import com.db4o.test.*;


public class TestCatSpeed {
    
	private final static String FILENAME="catspeed.yap";
    private final static int[] COUNT = {10000,100000};
	
	private final static Predicate[] PREDICATES={
		new Predicate() {
            public boolean match(Cat cat){
                return cat.getAge() < 5000;
            }
		},
		new Predicate() {
            public boolean match(Cat cat){
                return cat.getAge() > 2500 && cat.getAge() < 7500;
            }
		},
		new Predicate() {
            public boolean match(Cat cat){
                return cat.getFirstName().equals("SpeedyClone5000");
            }
		},
		new Predicate() {
            public boolean match(Cat cat){
                return cat.getAge() < 2500 
                		|| cat.getAge() > 7500
                		||cat.getFirstName().equals("SpeedyClone5000");
            }
		},
		new Predicate() {
            public boolean match(Cat cat){
                return cat.getFather()!=null
                		&&cat.getFather().getAge() < 5000;
            }
		},
		new Predicate() {
            public boolean match(Cat cat){
                return cat.getFather()!=null
                		&&(cat.getFather().getAge() < 5000 
                			|| cat.getFather().getFirstName().equals("SpeedyClone7500"));
            }
		},
	};

	public static void main(String[] args) {
		Db4o.configure().freespace().useRamSystem();
		ObjectClass objectClass = Db4o.configure().objectClass(Cat.class);
		objectClass.objectField("_firstName").indexed(true);
		objectClass.objectField("_lastName").indexed(true);
		objectClass.objectField("_age").indexed(true);
		objectClass.objectField("_father").indexed(true);
		objectClass.objectField("_mother").indexed(true);
    	for(int countIdx=0;countIdx<COUNT.length;countIdx++) {
    		storeCats(COUNT[countIdx]);
    		queryCats();
    	}
	}

	public static void queryCats() {
		for(int run=0;run<2;run++) {
			boolean warmup=(run==0);
			System.out.println(warmup ? "WARMING UP" : "TIMING");
			for(int predIdx=0;predIdx<PREDICATES.length;predIdx++) {
				if(!warmup) {
					System.out.println("PREDICATE #"+(predIdx+1));
				}
				timeQuery(PREDICATES[predIdx],true,!warmup);
				timeQuery(PREDICATES[predIdx],false,!warmup);
			}
		}
	}
	
	public static void timeQuery(Predicate predicate,boolean optimize,boolean time) {
		Db4o.configure().optimizeNativeQueries(optimize);
		ObjectContainer db=Db4o.openFile(FILENAME);
		long start=System.currentTimeMillis();
		ObjectSet result=db.query(predicate);
		if(time) {
			System.out.println("Found "+result.size()+" results in "+(System.currentTimeMillis()-start)+" ms (opt: "+optimize+")");
		}
		db.close();
	}
	
    public static void storeCats(int count){
    	System.out.println("STORING "+count+" CATS");
    	new File(FILENAME).delete();
    	ObjectContainer db=Db4o.openFile(FILENAME);
    	Cat lastCat=null;
        for (int i = 0; i < count; i++) {
            Cat fastCat = new Cat();
            fastCat._firstName = "SpeedyClone" + i;
            fastCat._age = i;
            fastCat._father=lastCat;
            db.set(fastCat);
            if(i%50000==0) {
            	db.commit();
            }
            lastCat=fastCat;
        }
        db.close();
    }
}
