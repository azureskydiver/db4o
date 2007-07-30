/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.staging;

import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class UntypedEvaluationTestCase extends AbstractDb4oTestCase {

	public static class Data {
		public int _id;

		public Data(int id) {
			_id = id;
		}
	}

	public static class UntypedEvaluation implements Evaluation {
		public void evaluate(Candidate candidate) {
			candidate.include(false);
		}
	}

	protected void store() throws Exception {
		store(new Data(42));
	}
	
	public void testUntypedEvaluation() {
		Query query = newQuery(Object.class); // replace with Data.class -> green
		query.constrain(new UntypedEvaluation());
		Assert.areEqual(0, query.execute().size());
	}

}
