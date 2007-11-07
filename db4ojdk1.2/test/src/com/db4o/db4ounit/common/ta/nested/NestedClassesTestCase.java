/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.ta.nested;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * TODO: This test case will fail when run against JDK1.3/JDK1.4 (though it will run green against
 * JDK1.2 and JDK1.5+) because the synthetic "this$0" field is final.
 * See http://developer.db4o.com/Resources/view.aspx/Reference/Implementation_Strategies/Type_Handling/Final_Fields/Final_Fields_Specifics
 */
public class NestedClassesTestCase
	extends AbstractDb4oTestCase
	implements OptOutTA {
	
	private OuterClass _outerObject;

	public static void main(String[] args) {
		new NestedClassesTestCase().runSolo();
	}
	
	protected void store() throws Exception {
		_outerObject = new OuterClass();
		_outerObject._foo = 10;
		
		final Activatable objOne = (Activatable)_outerObject.createInnerObject();
		store(objOne);
		
		final Activatable objTwo = (Activatable)_outerObject.createInnerObject();
		store(objTwo);
	}

	
	protected void configure(Configuration config) throws Exception {
		config.add(new TransparentActivationSupport());
	}
	
	public void test() throws Exception {
		ObjectSet query = db().query(OuterClass.InnerClass.class);
		while(query.hasNext()){
			OuterClass.InnerClass innerObject = (OuterClass.InnerClass) query.next();
			Assert.isNull(innerObject.getOuterObjectWithoutActivation());
			Assert.areEqual(_outerObject.foo(), innerObject.getOuterObject().foo());
		}
	}
	
}
