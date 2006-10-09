/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ObjectSet;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class ObjectSetIDs extends ClientServerTestCase {
	
	static final int COUNT = 11;
	
	public void store(ExtObjectContainer oc){
		for (int i = 0; i < COUNT; i++) {
			oc.set(new ObjectSetIDs());
        }
	}
	
	public void conc(ExtObjectContainer oc){
		Query q = oc.query();
		q.constrain(this.getClass());
		ObjectSet res = q.execute();
		Assert.areEqual(COUNT,res.size());
		long[] ids1 = new long[res.size()];
		int i =0;
		while(res.hasNext()){
			ids1[i++]=oc.getID(res.next());
		}
		
		res.reset();
		long[] ids2 = res.ext().getIDs();
		
		Assert.areEqual(COUNT,ids1.length);
		Assert.areEqual(COUNT,ids2.length);
		
		for (int j = 0; j < ids1.length; j++) {
			boolean found = false;
			for (int k = 0; k < ids2.length; k++) {
				if(ids1[j] == ids2[k]){
					found = true;
					break;
				}
			}
			Assert.isTrue(found);
        }
	}
}
