package com.db4o.db4ounit.jre11.btree;

import com.db4o.db4ounit.common.btree.BTreeTestCaseBase;
import com.db4o.foundation.ArgumentNullException;

import db4ounit.*;

public class BTreeNullKeyTestCase extends BTreeTestCaseBase {	

	public void testKeysCantBeNull() {
		final Integer value = null;
		Assert.expect(ArgumentNullException.class, new CodeBlock() {
			public void run() throws Exception {
				add(value);
			}
		});
	}

	private void add(Object element) {
		_btree.add(trans(), element);
	}
	
	public static void main(String[] args) {
		new BTreeNullKeyTestCase().runSolo();
	}
}
