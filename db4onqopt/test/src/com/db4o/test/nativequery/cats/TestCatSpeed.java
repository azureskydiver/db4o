/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.nativequery.cats;

import java.io.*;

import javax.naming.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;

public class TestCatSpeed {
    
	private final static String FILENAME="catspeed.yap";
    private final static int[] COUNT = {10000};
    // private final static int[] COUNT = {10000,100000};
	private static final int NUMRUNS = 5;
    
    private static final int ONLY_RUN_PREDICATE_NR = -1;
    private  static final boolean NQ_NOPT = true;
    private  static final boolean NQ_OPT = true;
    private  static final boolean SODA = true;
    
	private final static SodaCatPredicate[]      PREDICATES={
		new SodaCatPredicate() {
            public boolean match(Cat cat){
                return cat.getAge() < 800;
            }
            public void constrain(Query q) {
                q.descend("_age").constrain(new Integer(800)).smaller();
            }
		},
        new SodaCatPredicate() {
            public boolean match(Cat cat){
                return cat.getAge() > lower() && cat.getAge() < upper();
            }
            public void constrain(Query q) {
                Query qa = q.descend("_age");
                qa.constrain(new Integer(lower())).greater().and(
                qa.constrain(new Integer(upper())).smaller());
            }
        },
		new SodaCatPredicate() {
            public boolean match(Cat cat){
                return cat.getFirstName().equals("SpeedyClone991");
            }
            public void constrain(Query q) {
                q.descend("_firstName").constrain("SpeedyClone991");
            }

		},
		new SodaCatPredicate() {
            public boolean match(Cat cat){
                return cat.getAge() < 750 
                		|| cat.getAge() > 900
                		|| cat.getFirstName().equals("SpeedyClone888");
            }
            public void constrain(Query q) {
                Query qa = q.descend("_age");
                Constraint ca1 = qa.constrain(new Integer(750)).smaller();
                Constraint ca2 = qa.constrain(new Integer(900)).greater();
                Constraint cn = q.descend("_firstName").constrain("SpeedyClone888");
                ca1.or(ca2).or(cn);
            }

		},
		new SodaCatPredicate() {
            public boolean match(Cat cat){
                return cat.getFather()!=null
                	&& cat.getFather().getAge() < 900;
            }
            public void constrain(Query q) {
                Query qf = q.descend("_father");
                qf.constrain(null).not();
                qf.descend("_age").constrain(new Integer(900)).smaller();
            }

		},
		new SodaCatPredicate() {
            public boolean match(Cat cat){
                return cat.getFather()!=null
                    &&(cat.getFather().getAge() < 900 
                    || cat.getFather().getFirstName().equals("SpeedyClone933"));
            }
            public void constrain(Query q) {
                Query qf = q.descend("_father");
                Constraint c1 = qf.constrain(null).not();
                Constraint c2 = qf.descend("_age").constrain(new Integer(900)).smaller();
                Constraint c3 = qf.descend("_firstName").constrain("SpeedyClone933");
                c2.or(c3);
            }
		},
		new SodaCatPredicate() {
            public boolean match(Cat cat){
                return cat.getAge()<100
                	||(cat.getAge()>200&&cat.getAge()<300)
                	||cat.getAge()<400&&cat.getFirstName().equals("SpeedyClone150");
            }
            public void constrain(Query q) {
                Query qf = q.descend("_age");
                Constraint c1 = qf.constrain(new Integer(100)).smaller();
                Constraint c2 = qf.constrain(new Integer(200)).greater().and(qf.constrain(new Integer(300)).smaller());
                Constraint c3 = qf.constrain(new Integer(400)).smaller().and(q.descend("_firstName").constrain("SpeedyClone150"));
                c1.or(c2).or(c3);
            }
		}
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
            for (int predIdx = 0; predIdx < PREDICATES.length; predIdx++) {
                PREDICATES[predIdx].setCount(COUNT[countIdx]);
            }
            if(ONLY_RUN_PREDICATE_NR > 0){
                queryCats(ONLY_RUN_PREDICATE_NR - 1);
            }else{
                queryCats();
            }
    	}
	}

	public static void queryCats() {
		for (int predIdx = 0; predIdx < PREDICATES.length; predIdx++) {
            queryCats(predIdx);
		}
	}
    
    private static void queryCats(int predIdx){
        long timeUnopt = 0;
        long timeOpt = 0;
        long timeSoda = 0;
        for (int run = 0; run <= NUMRUNS; run++) {
            boolean warmup = (run == 0);
            if(NQ_NOPT){
                timeUnopt += timeQuery(PREDICATES[predIdx], false, warmup);
            }
            if(NQ_OPT){
                timeOpt += timeQuery(PREDICATES[predIdx], true, warmup);
            }
            if(SODA){
                timeSoda += timeSoda(PREDICATES[predIdx], warmup);
            }
        }
        System.out.println("PREDICATE #" + (predIdx + 1)+": "+(timeUnopt/NUMRUNS)+" / "+(timeOpt/NUMRUNS)+" / "+(timeSoda/NUMRUNS));
    }
    
    
	public static long timeQuery(Predicate predicate, boolean optimize,
			boolean warmup) {
		Db4o.configure().optimizeNativeQueries(optimize);
		ObjectContainer db = Db4o.openFile(FILENAME);
		long start = System.currentTimeMillis();
		db.query(predicate);
		long time = (warmup ? 0 : System.currentTimeMillis() - start);
		db.close();
		return time;
	}
    
    public static long timeSoda(SodaCatPredicate predicate, boolean warmup){
        ObjectContainer db = Db4o.openFile(FILENAME);
        long start = System.currentTimeMillis();
        predicate.sodaQuery(db);
        long time = (warmup ? 0 : System.currentTimeMillis() - start);
        db.close();
        return time;
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


/*


Tuning history:


Tuesday November 1 23:20
Starting out on 5.0.008

STORING 10000 CATS
PREDICATE #1: 918 / 25 / 12
PREDICATE #2: 896 / 187 / 18
PREDICATE #3: 922 / 16 / 3
PREDICATE #4: 915 / 381 / 275
PREDICATE #5: 931 / 703 / 15190
PREDICATE #6: 890 / 846 / 803

What's wrong with #5 Soda ???

Wednesday November 2 1:50
Introduced a limit where index traversal should stop.

STORING 10000 CATS
PREDICATE #1: 918 / 25 / 15
PREDICATE #2: 884 / 193 / 15
PREDICATE #3: 918 / 6 / 0
PREDICATE #4: 937 / 346 / 256
PREDICATE #5: 915 / 822 / 750
PREDICATE #6: 909 / 918 / 819


Wednesday November 2 14:00
Using ANDed joined constraints to load initial candidates

STORING 10000 CATS
PREDICATE #1: 906 / 27 / 15
PREDICATE #2: 900 / 31 / 15
PREDICATE #3: 906 / 15 / 0
PREDICATE #4: 965 / 403 / 321
PREDICATE #5: 868 / 731 / 668
PREDICATE #6: 881 / 881 / 828


Wednesday November 2 15:00
Modified #2 to show the deficiency on ranges

STORING 10000 CATS
PREDICATE #1: 906 / 21 / 15
PREDICATE #2: 884 / 187 / 178
PREDICATE #3: 912 / 9 / 3
PREDICATE #4: 925 / 346 / 247
PREDICATE #5: 887 / 731 / 718
PREDICATE #6: 884 / 893 / 818

Thursday November 3 04:00 (pr)
Added #7 for compound boolean checks
Before:
PREDICATE #7: 279 / 1304 / 139
After:
PREDICATE #7: 307 / 172 / 148


Monday November 7 1:00
AND tuning completed, results are timed with 
com.db4o.Debug.useNIxPaths = true 

STORING 10000 CATS
PREDICATE #1: 900 / 31 / 15
PREDICATE #2: 893 / 31 / 15
PREDICATE #3: 896 / 15 / 3
PREDICATE #4: 890 / 281 / 250
PREDICATE #5: 887 / 703 / 725
PREDICATE #6: 862 / 840 / 831
PREDICATE #7: 874 / 353 / 356


*/
