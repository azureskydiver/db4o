/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.querying;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CascadedDeleteUpdate extends AbstractDb4oTestCase {

	public static class CduHelper {
		Object parent1;
		Object parent2;
	}
	
	public Object child;
	
	protected void configure(Configuration config) {
		config.objectClass(this).cascadeOnDelete(true);
		config.objectClass(CduHelper.class).cascadeOnDelete(true);
	}
	
	protected void store() {
		CascadedDeleteUpdate cdu1 = new CascadedDeleteUpdate();
		CascadedDeleteUpdate cdu2 = new CascadedDeleteUpdate();
		
		CduHelper helper = new CduHelper();
		helper.parent1 = cdu1;
		helper.parent2 = cdu2;
		cdu1.child = helper; 
		cdu2.child = helper;
		
		db().set(cdu1);
		db().set(cdu2);
		db().set(cdu1);
		db().set(cdu2);
	}
	
	public void test(){
		Query q = newQuery();
		q.constrain(this.getClass());
		ObjectSet objectSet = q.execute();
		
		while(objectSet.hasNext()){
			CascadedDeleteUpdate cdu = (CascadedDeleteUpdate) objectSet.next();
			Assert.isNotNull(cdu.child);
		}
	}
}
