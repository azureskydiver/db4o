/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.ext.ExtObjectContainer;

import db4ounit.extensions.ClientServerTestCase;
import db4ounit.extensions.Db4oUtil;

/**
 * 
 */
public class CascadeDeleteFalse extends ClientServerTestCase {

	public CascadeDeleteFalseHelper h1;

	public CascadeDeleteFalseHelper h2;

	public CascadeDeleteFalseHelper h3;

	protected void configure(Configuration config) {
		config.objectClass(this).cascadeOnDelete(true);
		config.objectClass(this).objectField("h3").cascadeOnDelete(
				false);
	}

	public void store(ExtObjectContainer oc) {
		h1 = new CascadeDeleteFalseHelper();
		h2 = new CascadeDeleteFalseHelper();
		h3 = new CascadeDeleteFalseHelper();
		oc.set(this);
	}

	public void concDelete(ExtObjectContainer oc) {
		ObjectSet os = oc.query(CascadeDeleteFalse.class);
		if (os.size() == 0) { // the object has been deleted
			return;
		}
		CascadeDeleteFalse cdf = (CascadeDeleteFalse) os.next();
		try {
			// sleep 1000 ms, waiting for other threads.
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		oc.delete(cdf);
		// FIXME: following assertion fails sometimes
		Db4oUtil.assertOccurrences(oc, CascadeDeleteFalse.class, 0);
		Db4oUtil.assertOccurrences(oc, CascadeDeleteFalseHelper.class, 1);
	}

	public void checkDelete(ExtObjectContainer oc) {
		Db4oUtil.assertOccurrences(oc, CascadeDeleteFalse.class, 0);
		Db4oUtil.assertOccurrences(oc, CascadeDeleteFalseHelper.class, 1);
	}

	public static class CascadeDeleteFalseHelper {

	}
}
