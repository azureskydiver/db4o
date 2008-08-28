/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import java.util.*;

import com.db4o.*;
import com.db4o.query.*;


/**
 * @decaf.ignore.jdk11
 */
public class JdkComparatorSort {
	private static class AscendingIdComparator implements Comparator {
		public int compare(Object first, Object second) {
			return ((JdkComparatorSort)first)._id-((JdkComparatorSort)second)._id;
		}
	}

	private static class DescendingIdComparator implements Comparator {
		public int compare(Object first, Object second) {
			return ((JdkComparatorSort)second)._id-((JdkComparatorSort)first)._id;
		}
	}

	private static class OddEvenIdComparator implements Comparator {
		public int compare(Object first, Object second) {
			int idA=((JdkComparatorSort)first)._id;
			int idB=((JdkComparatorSort)second)._id;
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
			return ((JdkComparatorSort)first)._name.compareTo(((JdkComparatorSort)second)._name);
		}
	}

	private static class SmallerThanThreePredicate extends Predicate<JdkComparatorSort> {
		public boolean match(JdkComparatorSort candidate) {
			return candidate._id<3;
		}
	}
	
	public int _id;
	public String _name;
	
	public JdkComparatorSort() {
		this(0,null);
	}
	
	public JdkComparatorSort(int id, String name) {
		this._id = id;
		this._name = name;
	}

	public void store() {
		for(int i=0;i<4;i++) {
			Test.store(new JdkComparatorSort(i,String.valueOf(3-i)));
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

	public void testByIdAscendingNQ() {
		ObjectSet result=Test.objectContainer().query(new SmallerThanThreePredicate(),new AscendingIdComparator());
		assertIdOrder(result,new int[]{0,1,2});
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

	public void testByIdDescendingNQ() {
		ObjectSet result=Test.objectContainer().query(new SmallerThanThreePredicate(),new DescendingIdComparator());
		assertIdOrder(result,new int[]{2,1,0});
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

	public void testByIdOddEvenNQ() {
		ObjectSet result=Test.objectContainer().query(new SmallerThanThreePredicate(),new OddEvenIdComparator());
		assertIdOrder(result,new int[]{0,2,1});
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
	
	public void testByNameAscendingNQ() {
		ObjectSet result=Test.objectContainer().query(new SmallerThanThreePredicate(),new AscendingNameComparator());
		assertIdOrder(result,new int[]{2,1,0});
	}

	private void assertIdOrder(Comparator comparator,int[] ids) {
		Query query=Test.query();
		query.constrain(getClass());
		assertIdOrder(query,comparator,ids);
	}

	private void assertIdOrder(Query query,Comparator comparator,int[] ids) {
		query.sortBy(comparator);
		ObjectSet result=query.execute();
		assertIdOrder(result,ids);
	}

	private void assertIdOrder(ObjectSet result,int[] ids) {
		Test.ensureEquals(ids.length,result.size());
		for (int idx = 0; idx < ids.length; idx++) {
			Test.ensureEquals(ids[idx], ((JdkComparatorSort)result.next())._id);
		}
	}
	
	public static void main(String[] args) {
		Test.run(JdkComparatorSort.class);
	}
}
