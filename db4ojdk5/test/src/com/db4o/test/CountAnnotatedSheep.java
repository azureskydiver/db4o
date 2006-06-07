package com.db4o.test;

import com.db4o.*;
import com.db4o.query.*;

public class CountAnnotatedSheep {
	public final static int NUMSHEEP=10;
	
	public void configure() {
		Db4o.configure().activationDepth(0);
	}
	
	public void store() {
		Sheep parent=null;
		SheepNotAnnotated noParent=null;
		for(int i=0;i<10;i++) {
			Sheep sheep=new Sheep(String.valueOf(i+1),parent);
			SheepNotAnnotated noSheep=new SheepNotAnnotated(sheep.getName(),noParent);
			Test.store(sheep);
			Test.store(noSheep);
			parent=sheep;
			noParent=noSheep;
		}
	}
	
	public void testRead() {
		Test.objectContainer().purge();
		Query sheepQuery=Test.query();
		sheepQuery.constrain(Sheep.class);
		sheepQuery.descend("name").constrain(String.valueOf(NUMSHEEP));
		ObjectSet<Sheep> sheep=sheepQuery.execute();
		Test.ensureEquals(1,sheep.size());
		Sheep curSheep=sheep.next();
		int sheepCount=1;
		while(curSheep.parent!=null) {
			curSheep=curSheep.parent;
			sheepCount++;
		}
		Test.ensureEquals(NUMSHEEP,sheepCount);

		Query noSheepQuery=Test.query();
		noSheepQuery.constrain(SheepNotAnnotated.class);
		noSheepQuery.descend("name").constrain(String.valueOf(NUMSHEEP));
		ObjectSet<SheepNotAnnotated> noSheep=noSheepQuery.execute();
		Test.ensureEquals(1,noSheep.size());
		SheepNotAnnotated curNoSheep=noSheep.next();
		Test.ensure(curNoSheep.parent==null);
	}
	
	public static void main(String[] args) {
		Test.runSolo(CountAnnotatedSheep.class);
	}
}
