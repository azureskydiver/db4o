/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.soda.classes.untypedhierarchy; // Generierter package-Name

import com.db4o.query.*;
import com.db4o.test.soda.*;
import com.db4o.test.soda.engines.db4o.*;

/**
 * epaul:
 * Shows a bug.
 * 
 * carlrosenberger:
 * Fixed!
 * The error was due to the the behaviour of STCompare.java.
 * It compared the syntetic fields in inner classes also.
 * I changed the behaviour to neglect all fields that
 * contain a "$".
 * 
 *
 * @author <a href="mailto:Paul-Ebermann@gmx.de">Paul Ebermann</a>
 * @version 0.1
 */
public class STInnerClasses implements STClass 
{

	public static transient SodaTest st;
	
	public class Parent
	{
		public Object child;
		public Parent(Object o) { child = o; }
		public String toString() { return "Parent[" + child + "]"; }
		public Parent() {}
	}


	public class FirstClass
	{
		public Object childFirst;
		public FirstClass(Object o ) { childFirst = o; }
		public String toString() { return "First[" + childFirst + "]"; }
		public FirstClass() {}
	}

	public STInnerClasses ()
	{
	}

	public Object[] store() {
		return new Object[]
			{
				new Parent(new FirstClass("Example")),
				new Parent(new FirstClass("no Example")),
			};
	}

	/**
	 * Only 
	 */
	public void testNothing()
	{
		Query q = st.query();
		Query q2 = q.descend("child");
		Object[] r = store();
		st.expect(q, r);
		//SodaTest.log(q);
	}

	/**
	 * Start the test.
	 */
	public static void main(String[] params)
	{
		new SodaTest().run(new STClass[] { new STInnerClasses()}, new STEngine[] {new STDb4o()}, false);

	}


	
}// STSomeClasses
