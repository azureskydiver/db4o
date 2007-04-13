/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.cs.common.util.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class IsStored extends AbstractDb4oTestCase {
	
	String myString;
	
	public void conc(ExtObjectContainer oc){
		IsStored isStored = new IsStored();
		isStored.myString = "isStored";
		oc.set(isStored);
		Assert.isTrue(oc.isStored(isStored));
		Db4oUtil.assertOccurrences(oc,IsStored.class,1);
		oc.delete(isStored);
		Assert.isFalse(oc.isStored(isStored));
		oc.rollback();
		Assert.isTrue(oc.isStored(isStored));
		oc.delete(isStored);
		Assert.isFalse(oc.isStored(isStored));
		Db4oUtil.assertOccurrences(oc,IsStored.class,0);
		oc.commit();
		Assert.isFalse(oc.isStored(isStored));
	}
	
	public void check(ExtObjectContainer oc){
		Db4oUtil.assertOccurrences(oc,IsStored.class,0);
	}
	
}
