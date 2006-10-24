/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.soda.util;

import com.db4o.query.*;

import db4ounit.extensions.*;

public abstract class SodaBaseTestCase extends AbstractDb4oTestCase {

	protected Object[] _array;

	protected void db4oSetupBeforeStore() throws Exception {
		_array=createData();
	}

	protected void store() {
		Object[] data=createData();
		for (int idx = 0; idx < data.length; idx++) {
			db().set(data[idx]);
		}
	}
	
	protected abstract Object[] createData();

    protected void expect(Query query, int[] indices) {
        SodaTestUtil.expect(query, collectCandidates(indices), false);
    }

    protected void expectOrdered(Query query, int[] indices) {
        SodaTestUtil.expectOrdered(query, collectCandidates(indices));
    }

	private Object[] collectCandidates(int[] indices) {
		Object[] data=new Object[indices.length];
    	for (int idx = 0; idx < indices.length; idx++) {
			data[idx]=_array[indices[idx]];
		}
		return data;
	}
}
