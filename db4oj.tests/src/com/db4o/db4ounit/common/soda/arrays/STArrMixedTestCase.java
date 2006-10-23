/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.soda.arrays;

import com.db4o.db4ounit.common.soda.util.*;
import com.db4o.query.*;

public class STArrMixedTestCase extends SodaBaseTestCase {
	
	public static class Data {
		public Object[] _arr;

		public Data(Object[] arr) {
			this._arr = arr;
		}
	}

	protected Object[] createData() {
		Object[] arr=new Object[]{
			new Data(null),
			new Data(new Object[0]),
			new Data(new Object[] {new Integer(0), new Integer(0), "foo", new Boolean(false)}),
			new Data(new Object[] {new Integer(1), new Integer(17), new Integer(Integer.MAX_VALUE - 1), "foo", "bar"}),
			new Data(new Object[] {new Integer(3), new Integer(17), new Integer(25), new Integer(Integer.MAX_VALUE - 2)})
		};
		return arr;
	}
	
	public void testDefaultContainsInteger(){
		Query q = newQuery();
		q.constrain(new Data(new Object[] {new Integer(17)}));
		expect(q, new int[] {3,4});
	}
	
	public void testDefaultContainsString(){
		Query q = newQuery();
		q.constrain(new Data(new Object[] {"foo"}));
		expect(q, new int[] {2,3});
	}
	
	public void testDefaultContainsBoolean(){
		Query q = newQuery();
		q.constrain(new Data(new Object[] {new Boolean(false)}));
		expect(q, new int[] {2});
	}

	public void testDefaultContainsTwo(){
		Query q = newQuery();
		q.constrain(new Data(new Object[] {new Integer(17), "bar"}));
		expect(q, new int[] {3});
	}
	
	public void testDescendOne(){
		Query q = newQuery(Data.class);
		q.descend("_arr").constrain(new Integer(17));
		expect(q, new int[] {3,4});
	}
	
	public void testDescendTwo(){
		Query q = newQuery(Data.class);
		Query qElements = q.descend("_arr");
		qElements.constrain(new Integer(17));
		qElements.constrain("bar");
		expect(q, new int[] {3});
	}
	
	public void testDescendSmaller(){
		Query q = newQuery(Data.class);
		Query qElements = q.descend("_arr");
		qElements.constrain(new Integer(3)).smaller();
		expect(q, new int[] {2,3});
	}
	
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	