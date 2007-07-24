/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.test.concurrency;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;

import db4ounit.extensions.*;

public class CascadeDeleteFalseTestCase extends Db4oClientServerTestCase {
	
	public static void main(String[] args) {
		new CascadeDeleteFalseTestCase().runConcurrency();
	}

	public CascadeDeleteFalseHelper h1;

	public CascadeDeleteFalseHelper h2;

	public CascadeDeleteFalseHelper h3;

	protected void configure(Configuration config) {
		config.objectClass(this).cascadeOnDelete(true);
		config.objectClass(this).objectField("h3").cascadeOnDelete(false);
	}

	protected void store() {
		h1 = new CascadeDeleteFalseHelper();
		h2 = new CascadeDeleteFalseHelper();
		h3 = new CascadeDeleteFalseHelper();
		store(this);
	}

	public void concDelete(ExtObjectContainer oc) throws Exception {
		ObjectSet os = oc.query(CascadeDeleteFalseTestCase.class);
		if (os.size() == 0) { // the object has been deleted
			return;
		}
		if(! os.hasNext()){
			// object can be deleted after query 
			return;
		}
		CascadeDeleteFalseTestCase cdf = (CascadeDeleteFalseTestCase) os.next();
		// sleep 1000 ms, waiting for other threads.
		// Thread.sleep(500);
		oc.delete(cdf);
		oc.commit();
		assertOccurrences(oc, CascadeDeleteFalseTestCase.class, 0);
		assertOccurrences(oc, CascadeDeleteFalseHelper.class, 1);
	}

	public void checkDelete(ExtObjectContainer oc) {
		assertOccurrences(oc, CascadeDeleteFalseTestCase.class, 0);
		assertOccurrences(oc, CascadeDeleteFalseHelper.class, 1);
	}

	public static class CascadeDeleteFalseHelper {

	}
}
