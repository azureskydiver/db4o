/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.soda.arrays;

import com.db4o.db4ounit.common.soda.util.*;
import com.db4o.query.*;

public class STArrStringTTestCase extends SodaBaseTestCase {
	
	public static class Data {
		public String[] _strArr;

		public Data(String[] arr) {
			_strArr = arr;
		}
	}
	
	protected Object[] createData() {
		return new Object[]{
			new Data(null),
			new Data(new String[] {null}),
			new Data(new String[] {null, null}),
			new Data(new String[] {"foo", "bar", "fly"}),
			new Data(new String[] {null, "bar", "wohay", "johy"})
		};
	}
	
	public void testDefaultContainsOne(){
		Query q = newQuery();
		q.constrain(new Data(new String[] {"bar"}));
		expect(q, new int[] {3,4});
	}
	
	public void testDefaultContainsTwo(){
		Query q = newQuery();
		q.constrain(new Data(new String[] {"foo", "bar"}));
		expect(q, new int[] {3});
	}
	
	public void testDescendOne(){
		Query q = newQuery(Data.class);
		q.descend("_strArr").constrain("bar");
		expect(q, new int[] {3,4});
	}
	
	public void testDescendTwo(){
		Query q = newQuery(Data.class);
		Query qElements = q.descend("_strArr");
		qElements.constrain("foo");
		qElements.constrain("bar");
		expect(q, new int[] {3});
	}
	
	public void testDescendOneNot(){
		Query q = newQuery(Data.class);
		q.descend("_strArr").constrain("bar").not();
		expect(q, new int[] {0,1,2});
	}
	
	public void testDescendTwoNot(){
		Query q = newQuery(Data.class);
		Query qElements = q.descend("_strArr");
		qElements.constrain("foo").not();
		qElements.constrain("bar").not();
		expect(q, new int[] {0,1,2});
	}
}
	
