package com.db4o.test.soda;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.*;

public class SodaComparatorSort {
	private static class AscendingIdComparator implements QueryComparator {
		public int compare(Object first, Object second) {
			return ((SodaComparatorSort)first)._id-((SodaComparatorSort)second)._id;
		}
	}

	private static class DescendingIdComparator implements QueryComparator {
		public int compare(Object first, Object second) {
			return ((SodaComparatorSort)second)._id-((SodaComparatorSort)first)._id;
		}
	}

	private static class OddEvenIdComparator implements QueryComparator {
		public int compare(Object first, Object second) {
			int idA=((SodaComparatorSort)first)._id;
			int idB=((SodaComparatorSort)second)._id;
			int modA=idA%2;
			int modB=idB%2;
			if(modA!=modB) {
				return modA-modB;
			}
			return idA-idB;
		}
	}

	private static class AscendingNameComparator implements QueryComparator {
		public int compare(Object first, Object second) {
			return ((SodaComparatorSort)first)._name.compareTo(((SodaComparatorSort)second)._name);
		}
	}

	public int _id;
	public String _name;
	
	public SodaComparatorSort() {
		this(0,null);
	}
	
	public SodaComparatorSort(int id, String name) {
		this._id = id;
		this._name = name;
	}

	public void store() {
		for(int i=0;i<4;i++) {
			Test.store(new SodaComparatorSort(i,String.valueOf(3-i)));
		}
	}
	
	public void testByIdAscending() {
		assertIdOrder(new AscendingIdComparator(),new int[]{0,1,2,3});
	}

	public void testByIdAscendingConstrained() {
		Query query=Test.query();
		query.constrain(getClass());
		query.descend("_id").constrain(new Integer(3)).smaller();
		assertIdOrder(query,new AscendingIdComparator(),new int[]{0,1,2});
	}

	public void testByIdDescending() {
		assertIdOrder(new DescendingIdComparator(),new int[]{3,2,1,0});
	}

	public void testByIdDescendingConstrained() {
		Query query=Test.query();
		query.constrain(getClass());
		query.descend("_id").constrain(new Integer(3)).smaller();
		assertIdOrder(query,new DescendingIdComparator(),new int[]{2,1,0});
	}

	public void testByIdOddEven() {
		assertIdOrder(new OddEvenIdComparator(),new int[]{0,2,1,3});
	}

	public void testByIdOddEvenConstrained() {
		Query query=Test.query();
		query.constrain(getClass());
		query.descend("_id").constrain(new Integer(3)).smaller();
		assertIdOrder(query,new OddEvenIdComparator(),new int[]{0,2,1});
	}

	public void testByNameAscending() {
		assertIdOrder(new AscendingNameComparator(),new int[]{3,2,1,0});
	}

	public void testByNameAscendingConstrained() {
		Query query=Test.query();
		query.constrain(getClass());
		query.descend("_id").constrain(new Integer(3)).smaller();
		assertIdOrder(query,new AscendingNameComparator(),new int[]{2,1,0});
	}

	private void assertIdOrder(QueryComparator comparator,int[] ids) {
		Query query=Test.query();
		query.constrain(getClass());
		assertIdOrder(query,comparator,ids);
	}

	private void assertIdOrder(Query query,QueryComparator comparator,int[] ids) {
		query.sortBy(comparator);
		ObjectSet result=query.execute();
		Test.ensureEquals(result.size(), ids.length);
		for (int idx = 0; idx < ids.length; idx++) {
			Test.ensureEquals(ids[idx], ((SodaComparatorSort)result.next())._id);
		}
	}
}
