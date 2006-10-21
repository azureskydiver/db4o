/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.querying;


import db4ounit.extensions.*;


public class QueryResultTestCase extends AbstractDb4oTestCase {
	
	private static final int[] VALUES = new int[] {1 , 7, 9, 5, 6};

	public static void main(String[] args) {
		new QueryResultTestCase().runSolo();
	}
	
	
	
	protected void store() throws Exception {
		storeItems(VALUES);
	}
	
	protected void storeItems(final int[] foos) {
		for (int i = 0; i < foos.length; i++) {
			store(new Item(foos[i]));
	    }
	}
	
	public static class Item{
		
		public int foo;
		
		public Item() {
			
		}
		
		public Item(int foo_) {
			foo = foo_;
		}
		
	}
	
	
	
	

}
