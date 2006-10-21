/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.querying;


import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.btree.*;
import com.db4o.db4ounit.common.foundation.*;
import com.db4o.foundation.*;
import com.db4o.inside.query.*;
import com.db4o.query.*;

import db4ounit.extensions.*;


public abstract class QueryResultTestCase extends AbstractDb4oTestCase {
	
	private static final int[] VALUES = new int[] { 1 , 5, 6 , 7, 9};
	
	private final int [] ids = new int[VALUES.length];
	
	protected void configure(Configuration config) {
		indexField(config, Item.class, "foo");
	}
	
	public void testClassQuery(){
		Query query = newItemQuery();
		QueryResult queryResult = executeQuery(query); 
		assertIDs(queryResult, ids);
	}
	
	public void testIndexedFieldQuery(){
		Query query = newItemQuery();
		query.descend("foo").constrain(new Integer(6)).smaller();
		QueryResult queryResult = executeQuery(query);
		assertIDs(queryResult, new int[] {ids[0], ids[1] });
	}
	
	public void testNonIndexedFieldQuery(){
		Query query = newItemQuery();
		query.descend("bar").constrain(new Integer(6)).smaller();
		QueryResult queryResult = executeQuery(query);
		assertIDs(queryResult, new int[] {ids[0], ids[1] });
	}

	private QueryResult executeQuery(Query query) {
		QueryResult queryResult = newQueryResult();
		queryResult.loadFromQuery((QQuery)query);
		return queryResult;
	}
	
	private void assertIDs(QueryResult queryResult, int[] expectedIDs){
		ExpectingVisitor expectingVisitor = new ExpectingVisitor(IntArrays4.toObjectArray(expectedIDs));
		IntIterator4 i = queryResult.iterateIDs();
		while(i.moveNext()){
			expectingVisitor.visit(new Integer(i.currentInt()));
		}
		expectingVisitor.assertExpectations();
	}
	
	protected Query newItemQuery() {
		return newQuery(Item.class);
	}

	protected void store() throws Exception {
		storeItems(VALUES);
	}
	
	protected void storeItems(final int[] foos) {
		for (int i = 0; i < foos.length; i++) {
			Item item = new Item(foos[i]); 
			store(item);
			ids[i] = (int)db().getID(item);
	    }
	}
	
	public static class Item{
		
		public int foo;
		
		public int bar;
		
		public Item() {
			
		}
		
		public Item(int foo_) {
			foo = foo_;
			bar = foo;
		}
		
	}
	
	protected abstract QueryResult newQueryResult();
	
	
	
}
