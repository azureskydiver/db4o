/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre11.soda.collections;
import java.util.*;

import com.db4o.query.*;


public class STHashtableUTestCase extends com.db4o.db4ounit.common.soda.util.SodaBaseTestCase {
	
	public Object col;

	public STHashtableUTestCase() {
	}

	public STHashtableUTestCase(Object[] arr) {
		Hashtable ht = new Hashtable();
		for (int i = 0; i < arr.length; i++) {
			ht.put(arr[i], new Integer(i));
		}
		col = ht;
	}

	public Object[] createData() {
		return new Object[] {
			new STHashtableUTestCase(),
				new STHashtableUTestCase(new Object[0]),
				new STHashtableUTestCase(new Object[] { new Integer(0), new Integer(0)}),
				new STHashtableUTestCase(
					new Object[] {
						new Integer(1),
						new Integer(17),
						new Integer(Integer.MAX_VALUE - 1)}),
				new STHashtableUTestCase(
					new Object[] {
						new Integer(3),
						new Integer(17),
						new Integer(25),
						new Integer(Integer.MAX_VALUE - 2)}),
				new STHashtableUTestCase(new Object[] { "foo", new STElement("bar", "barbar")}),
				new STHashtableUTestCase(new Object[] { "foo2", new STElement("bar", "barbar2")})
		};
	}
	
	public void testDefaultContainsInteger() {
		Query q = newQuery();
		
		q.constrain(new STHashtableUTestCase(new Object[] { new Integer(17)}));
		expect(q, new int[] { 3, 4 });
	}

	public void testDefaultContainsString() {
		Query q = newQuery();
		
		q.constrain(new STHashtableUTestCase(new Object[] { "foo" }));
		expect(q, new int[] { 5 });
	}

	public void testDefaultContainsTwo() {
		Query q = newQuery();
		
		q.constrain(new STHashtableUTestCase(new Object[] { new Integer(17), new Integer(25)}));
		expect(q, new int[] { 4 });
	}

	public void testDescendOne() {
		Query q = newQuery();
		
		q.constrain(STHashtableUTestCase.class);
		q.descend("col").constrain(new Integer(17));
		expect(q, new int[] { 3, 4 });
	}

	public void testDescendTwo() {
		Query q = newQuery();
		
		q.constrain(STHashtableUTestCase.class);
		Query qElements = q.descend("col");
		qElements.constrain(new Integer(17));
		qElements.constrain(new Integer(25));
		expect(q, new int[] { 4 });
	}

	public void testDescendSmaller() {
		Query q = newQuery();
		
		q.constrain(STHashtableUTestCase.class);
		Query qElements = q.descend("col");
		qElements.constrain(new Integer(3)).smaller();
		expect(q, new int[] { 2, 3 });
	}
	
	public void testDefaultContainsObject() {
		Query q = newQuery();
		
		q.constrain(new STHashtableUTestCase(new Object[] { new STElement("bar", null)}));
		expect(q, new int[] { 5, 6 });
	}
	
	public void testDescendToObject() {
		Query q = newQuery();
		
		q.constrain(new STHashtableUTestCase());
		q.descend("col").descend("foo1").constrain("bar");
		expect(q, new int[] { 5, 6 });
	}

}