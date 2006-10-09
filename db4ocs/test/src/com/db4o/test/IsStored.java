/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ext.ExtObjectContainer;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;
import db4ounit.extensions.Db4oUtil;

public class IsStored extends ClientServerTestCase {
	
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
