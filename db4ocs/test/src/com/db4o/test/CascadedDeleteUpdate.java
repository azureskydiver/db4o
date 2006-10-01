/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.Db4o;
import com.db4o.ObjectSet;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

/**
 * 
 */
public class CascadedDeleteUpdate extends ClientServerTestCase {

	public Object child;

	public void configure() {
		Db4o.configure().objectClass(this).cascadeOnDelete(true);
		Db4o.configure().objectClass(CduHelper.class).cascadeOnDelete(true);
	}

	public void store(ExtObjectContainer oc) {
		CascadedDeleteUpdate cdu1 = new CascadedDeleteUpdate();
		CascadedDeleteUpdate cdu2 = new CascadedDeleteUpdate();
		CduHelper helper = new CduHelper();
		helper.parent1 = cdu1;
		helper.parent2 = cdu2;
		cdu1.child = helper;
		cdu2.child = helper;
		oc.set(cdu1);
		oc.set(cdu2);
		oc.set(cdu1);
		oc.set(cdu2);
	}

	public void conc(ExtObjectContainer oc) {
		Query q = oc.query();
		q.constrain(CascadedDeleteUpdate.class);
		ObjectSet objectSet = q.execute();
		while (objectSet.hasNext()) {
			CascadedDeleteUpdate cdu = (CascadedDeleteUpdate) objectSet.next();
			Assert.isNotNull(cdu.child);
		}
	}

	public static class CduHelper {
		Object parent1;
		Object parent2;
	}

}
