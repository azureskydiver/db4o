/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.query.*;


/**
 * 
 */
public class CascadedDeleteUpdate {
	
	public Object child;
	
	public void configure(){
		Db4o.configure().objectClass(this).cascadeOnDelete(true);
		Db4o.configure().objectClass(CduHelper.class).cascadeOnDelete(true);
	}
	
	public void store(){
		CascadedDeleteUpdate cdu1 = new CascadedDeleteUpdate();
		CascadedDeleteUpdate cdu2 = new CascadedDeleteUpdate();
		CduHelper helper = new CduHelper();
		helper.parent1 = cdu1;
		helper.parent2 = cdu2;
		cdu1.child = helper; 
		cdu2.child = helper;
		Test.store(cdu1);
		Test.store(cdu2);
		Test.store(cdu1);
		Test.store(cdu2);
	}
	
	public void test(){
		Query q = Test.query();
		q.constrain(this.getClass());
		ObjectSet objectSet = q.execute();
		while(objectSet.hasNext()){
			CascadedDeleteUpdate cdu = (CascadedDeleteUpdate) objectSet.next();
			Test.ensure(cdu.child != null);
		}
	}
	
	public static class CduHelper{
		Object parent1;
		Object parent2;
	}

}
