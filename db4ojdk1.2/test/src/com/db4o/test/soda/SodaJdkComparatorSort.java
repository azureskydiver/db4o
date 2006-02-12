package com.db4o.test.soda;

import java.util.*;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.*;

public class SodaJdkComparatorSort {
	private static class AscendingIdComparator implements Comparator {
		public int compare(Object first, Object second) {
			return ((SodaJdkComparatorSort)first)._id-((SodaJdkComparatorSort)second)._id;
		}
	}

	private static class DescendingIdComparator implements Comparator {
		public int compare(Object first, Object second) {
			return ((SodaJdkComparatorSort)second)._id-((SodaJdkComparatorSort)first)._id;
		}
	}

	private static class OddEvenIdComparator implements Comparator {
		public int compare(Object first, Object second) {
			int idA=((SodaJdkComparatorSort)first)._id;
			int idB=((SodaJdkComparatorSort)second)._id;
			int modA=idA%2;
			int modB=idB%2;
			if(modA!=modB) {
				return modA-modB;
			}
			return idA-idB;
		}
	}

	private static class AscendingNameComparator implements Comparator {
		public int compare(Object first, Object second) {
			return ((SodaJdkComparatorSort)first)._name.compareTo(((SodaJdkComparatorSort)second)._name);
		}
	}

	public int _id;
	public String _name;
	
	public SodaJdkComparatorSort() {
		this(0,null);
	}
	
	public SodaJdkComparatorSort(int id, String name) {
		this._id = id;
		this._name = name;
	}

	public void store() {
		for(int i=0;i<4;i++) {
			Test.store(new SodaJdkComparatorSort(i,String.valueOf(3-i)));
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

	private void assertIdOrder(Comparator comparator,int[] ids) {
		Query query=Test.query();
		query.constrain(getClass());
		assertIdOrder(query,comparator,ids);
	}

	private void assertIdOrder(Query query,Comparator comparator,int[] ids) {
		query.sortBy(comparator);
		ObjectSet result=query.execute();
		Test.ensureEquals(result.size(), ids.length);
		for (int idx = 0; idx < ids.length; idx++) {
			Test.ensureEquals(ids[idx], ((SodaJdkComparatorSort)result.next())._id);
		}
	}
	
	public static void main(String[] args) {
		Test.run(SodaJdkComparatorSort.class);
	}
}
