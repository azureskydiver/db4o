package com.db4o.db4ounit.common.soda;

import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

public class NullIdentityConstraintTestCase extends AbstractDb4oTestCase {
	
	public static class Data {
		public Data _prev;

		public Data(Data prev) {
			this._prev = prev;
		}
	}
	
	protected void store() throws Exception {
		Data a=new Data(null);
		Data b=new Data(a);
		store(b);
	}
	
	public void testNullIdentity() {
		Query query=newQuery(Data.class);
		query.descend("_prev").constrain(null).identity();
		Assert.areEqual(1,query.execute().size());
	}
}
