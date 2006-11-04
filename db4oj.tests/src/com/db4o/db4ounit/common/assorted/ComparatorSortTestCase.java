/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;


import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class ComparatorSortTestCase extends AbstractDb4oTestCase {

	public static void main(String[] args) {
		new ComparatorSortTestCase().runSoloAndClientServer();
	}
	
	public static class Item{
		
		public int _id;
		
		public String _name;
		
		public Item(int id, String name) {
			_id = id;
			_name = name;
		}
		
	}
	
	public static class AscendingIdComparator implements QueryComparator {
		public int compare(Object first, Object second) {
			return ((Item)first)._id-((Item)second)._id;
		}
	}
	
	public void store() {
		for(int i=0;i<4;i++) {
			Item item = new Item(i,String.valueOf(3-i)); 
			store(item);
			int id = (int)db().getID(item);
		}
	}
	
	public void testByIdAscending() {
		assertIdOrder(new AscendingIdComparator(),new int[]{0,1,2,3});
	}
	
	private void assertIdOrder(QueryComparator comparator,int[] ids) {
		Query query= newQuery(Item.class);
		assertIdOrder(query,comparator,ids);
	}
	
	private void assertIdOrder(Query query, QueryComparator comparator, int[] ids) {
		query.sortBy(comparator);
		ObjectSet result=query.execute();
		assertIdOrder(result,ids);
	}
	
	private void assertIdOrder(ObjectSet result,int[] ids) {
		Assert.areEqual(ids.length,result.size());
		for (int idx = 0; idx < ids.length; idx++) {
			Assert.areEqual(ids[idx], ((Item)result.next())._id);
		}
	}
	

}
