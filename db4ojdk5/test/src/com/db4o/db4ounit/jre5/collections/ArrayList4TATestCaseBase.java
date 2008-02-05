/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections;

import com.db4o.collections.*;
import com.db4o.db4ounit.common.ta.*;
import com.db4o.ext.*;

import db4ounit.extensions.*;

/**
 * @exclude
 * 
 * @sharpen.ignore
 */
public class ArrayList4TATestCaseBase extends TransparentActivationTestCaseBase {
	
	@Override
	protected void store() throws Exception {
		ArrayList4<Integer> list = new ArrayList4<Integer>();
		ListAsserter.createList(list);
		store(list);
	}
	
	protected ArrayList4<Integer> retrieveAndAssertNullArrayList4() throws Exception{
		return CollectionsUtil.retrieveAndAssertNullArrayList4(db(), reflector());
	}
	
	protected ArrayList4<Integer> retrieveAndAssertNullArrayList4(ExtObjectContainer oc) throws Exception{
		return CollectionsUtil.retrieveAndAssertNullArrayList4(oc, reflector());
	}
	
	protected Db4oClientServerFixture clientServerFixture() {
		return (Db4oClientServerFixture) fixture();
	}
	
	protected ExtObjectContainer openNewClient() {
		return clientServerFixture().openNewClient();
	}

}
