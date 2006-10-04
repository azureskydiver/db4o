package com.db4o.db4ounit.jre11.assorted;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class StoreNumberTestCase extends AbstractDb4oTestCase {

	private static final int NUMENTRIES = 5;

	public static class Data {
		public Number _number;

		public Data(int value) {
			this._number = new Integer(value);
		}
	}

	protected void store() throws Exception {
		for(int i=0;i<NUMENTRIES;i++) {
			db().set(new Data(i));
		}
	}

	public void _testRetrieveAll() {
		Query query=db().query();
		query.constrain(Data.class);
		ObjectSet result=query.execute();
		Assert.areEqual(NUMENTRIES,result.size());
		while(result.hasNext()) {
			Data data=(Data)result.next();
			Assert.isNotNull(data._number);
		}
	}

	public void _testRetrieveNumber() {
		Query query=db().query();
		query.constrain(Data.class);
		query.descend("_number").constrain(new Integer(0));
		ObjectSet result=query.execute();
		Assert.areEqual(1,result.size());
	}
}
