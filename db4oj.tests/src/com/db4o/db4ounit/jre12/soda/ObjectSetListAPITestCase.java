/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.soda;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class ObjectSetListAPITestCase extends AbstractDb4oTestCase {

	private static final int NUMDATA = 1000;
	
	private static class Data {
		private int _id;

		public Data(int id) {
			_id = id;
			use(_id);
		}

		private void use(int id) {
		}
	}

	protected void configure(Configuration config) throws Exception {
		config.queries().evaluationMode(QueryEvaluationMode.LAZY);
	}
	
	protected void store() throws Exception {
		for(int i = 0; i < NUMDATA; i++) {
			store(new Data(i));
		}
	}
	
	public void testOutOfBounds() {
		final ObjectSet result = result();
		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				try {
					result.get(NUMDATA);
				}
				catch(Db4oException exc) {
					exc.printStackTrace();
				}
			}
		});
	}

	private ObjectSet result() {
		Query query = newQuery(Data.class);
		query.descend("_id").constrain(new Integer(Integer.MAX_VALUE)).not();
		final ObjectSet result = query.execute();
		return result;
	}
	
	public static void main(String[] args) {
		new ObjectSetListAPITestCase().runAll();
	}
}
