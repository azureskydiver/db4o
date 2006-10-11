/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.*;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class QueryForUnknownField extends ClientServerTestCase {

	public String _name;

	public QueryForUnknownField() {
	}

	public QueryForUnknownField(String name) {
		_name = name;
	}

	public void store(ExtObjectContainer oc) {
		_name = "name";
		oc.set(this);
	}

	public void conc(ExtObjectContainer oc) {
		Query q = oc.query();
		q.constrain(QueryForUnknownField.class);
		q.descend("_name").constrain("name");
		Assert.areEqual(1, q.execute().size());

		q = oc.query();
		q.constrain(QueryForUnknownField.class);
		q.descend("name").constrain("name");
		Assert.areEqual(0, q.execute().size());
	}

}
