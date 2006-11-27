/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.query.*;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

public class ComparatorSortTestCase extends AbstractDb4oTestCase {
	public static class AscendingIdComparator implements QueryComparator {
		public int compare(Object first, Object second) {
			return ((Item)first)._id-((Item)second)._id;
		}
	}

	public static class DescendingIdComparator implements QueryComparator {
		public int compare(Object first, Object second) {
			return ((Item)second)._id-((Item)first)._id;
		}
	}

	public static class OddEvenIdComparator implements QueryComparator {
		public int compare(Object first, Object second) {
			int idA=((Item)first)._id;
			int idB=((Item)second)._id;
			int modA=idA%2;
			int modB=idB%2;
			if(modA!=modB) {
				return modA-modB;
			}
			return idA-idB;
		}
	}

	public static class AscendingNameComparator implements QueryComparator {
		public int compare(Object first, Object second) {
			return ((Item)first)._name.compareTo(((Item)second)._name);
		}
	}

	public static class SmallerThanThreePredicate extends Predicate<Item> {
		public boolean match(Item candidate) {
			return candidate._id<3;
		}
	}
	
	public static class Item {
		public int _id;
		public String _name;
	
		public Item() {
			this(0,null);
		}
		
		public Item(int id, String name) {
			this._id = id;
			this._name = name;
		}
	}
	
	protected void configure(Configuration config) {	
		config.exceptionsOnNotStorable(true);
	}
	
	protected void store() {
		for(int i=0;i<4;i++) {
			store(new Item(i,String.valueOf(3-i)));
		}
	}
	
	public void testByIdAscending() {
		assertIdOrder(new AscendingIdComparator(),new int[]{0,1,2,3});
	}

	public void testByIdAscendingConstrained() {
		Query query=newItemQuery();
		query.descend("_id").constrain(new Integer(3)).smaller();
		assertIdOrder(query,new AscendingIdComparator(),new int[]{0,1,2});
	}

	public void testByIdAscendingNQ() {
		ObjectSet result=db().query(new SmallerThanThreePredicate(),new AscendingIdComparator());
		assertIdOrder(result,new int[]{0,1,2});
	}

	public void testByIdDescending() {
		assertIdOrder(new DescendingIdComparator(),new int[]{3,2,1,0});
	}

	public void testByIdDescendingConstrained() {
		Query query=newItemQuery();
		query.descend("_id").constrain(new Integer(3)).smaller();
		assertIdOrder(query,new DescendingIdComparator(),new int[]{2,1,0});
	}

	public void testByIdDescendingNQ() {
		ObjectSet result=db().query(new SmallerThanThreePredicate(),new DescendingIdComparator());
		assertIdOrder(result,new int[]{2,1,0});
	}

	public void testByIdOddEven() {
		assertIdOrder(new OddEvenIdComparator(),new int[]{0,2,1,3});
	}

	public void testByIdOddEvenConstrained() {
		Query query=newItemQuery();
		query.descend("_id").constrain(new Integer(3)).smaller();
		assertIdOrder(query,new OddEvenIdComparator(),new int[]{0,2,1});
	}

	public void testByIdOddEvenNQ() {
		ObjectSet result=db().query(new SmallerThanThreePredicate(),new OddEvenIdComparator());
		assertIdOrder(result,new int[]{0,2,1});
	}

	public void testByNameAscending() {
		assertIdOrder(new AscendingNameComparator(),new int[]{3,2,1,0});
	}

	public void testByNameAscendingConstrained() {
		Query query=newItemQuery();
		query.descend("_id").constrain(new Integer(3)).smaller();
		assertIdOrder(query,new AscendingNameComparator(),new int[]{2,1,0});
	}
	
	public void testByNameAscendingNQ() {
		ObjectSet result=db().query(new SmallerThanThreePredicate(),new AscendingNameComparator());
		assertIdOrder(result,new int[]{2,1,0});
	}

	private void assertIdOrder(QueryComparator comparator,int[] ids) {
		Query query=newItemQuery();
		assertIdOrder(query,comparator,ids);
	}

	private Query newItemQuery() {
		return newQuery(Item.class);
	}

	private void assertIdOrder(Query query,QueryComparator comparator,int[] ids) {
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
