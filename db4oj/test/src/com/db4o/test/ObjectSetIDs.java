/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;

public class ObjectSetIDs {
	
	static final int COUNT = 11;
	
	public void store(){
		Test.deleteAllInstances(this);
		for (int i = 0; i < COUNT; i++) {
			Test.store(new ObjectSetIDs());
        }
	}
	
	public void test(){
		ExtObjectContainer con = Test.objectContainer();
		Query q = Test.query();
		q.constrain(this.getClass());
		ObjectSet res = q.execute();
		long[] ids1 = new long[res.size()];
		int i =0;
		while(res.hasNext()){
			ids1[i++]=con.getID(res.next());
		}
		
		res.reset();
		long[] ids2 = res.ext().getIDs();
		
		Test.ensure(ids1.length == COUNT);
		Test.ensure(ids2.length == COUNT);
		
		for (int j = 0; j < ids1.length; j++) {
			boolean found = false;
			for (int k = 0; k < ids2.length; k++) {
				if(ids1[j] == ids2[k]){
					found = true;
					break;
				}
			}
			Test.ensure(found);
        }
	}
}
