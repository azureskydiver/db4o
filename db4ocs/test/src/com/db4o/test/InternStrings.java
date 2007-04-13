/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class InternStrings extends AbstractDb4oTestCase {
	public String _name;
	
	public InternStrings() {
		this(null);
	}

	public InternStrings(String name) {
		_name = name;
	}

	public void configure(Configuration config) {
		config.internStrings(true);
	}
	
	public void store(ExtObjectContainer oc) {
		String name="Foo";
		oc.set(new InternStrings(name));
		oc.set(new InternStrings(name));
	}
	
	public void conc(ExtObjectContainer oc) {
		Query query=oc.query();
		query.constrain(InternStrings.class);
		ObjectSet result=query.execute();
		Assert.areEqual(2, result.size());
		InternStrings first=(InternStrings)result.next();
		InternStrings second=(InternStrings)result.next();
		Assert.areSame(first._name, second._name);
	}
}
