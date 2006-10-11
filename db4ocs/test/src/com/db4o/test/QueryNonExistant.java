/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;
import db4ounit.extensions.Db4oUtil;

public class QueryNonExistant extends ClientServerTestCase {
	
	QueryNonExistant1 member;
	
	public QueryNonExistant(){
		// db4o constructor
	}
	
	public QueryNonExistant(boolean createMembers){
		member = new QueryNonExistant1();
		member.member = new QueryNonExistant2();
		member.member.member = this;
		// db4o constructor
	}
	
	public void conc(ExtObjectContainer oc){ 
		oc.get((new QueryNonExistant(true)));
		Db4oUtil.assertOccurrences(oc,QueryNonExistant.class, 0);
		Query q = oc.query();
		q.constrain(new QueryNonExistant(true));
		Assert.areEqual(0, q.execute().size());
	}
	
	public static class QueryNonExistant1{
		QueryNonExistant2 member;
	}
	
	public static class QueryNonExistant2 extends QueryNonExistant1{
		QueryNonExistant member;
	}
	
}
