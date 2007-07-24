/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.concurrency;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ComparatorSortTestCase extends Db4oClientServerTestCase {
	
	public static void main(String[] args) {
		new ComparatorSortTestCase().runConcurrency();
	}
	
	public static class AscendingIdComparator implements QueryComparator {
		public int compare(Object first, Object second) {
			return ((ComparatorSortTestCase) first)._id - ((ComparatorSortTestCase) second)._id;
		}
	}

	public static class DescendingIdComparator implements QueryComparator {
		public int compare(Object first, Object second) {
			return ((ComparatorSortTestCase) second)._id - ((ComparatorSortTestCase) first)._id;
		}
	}

	public static class OddEvenIdComparator implements QueryComparator {
		public int compare(Object first, Object second) {
			int idA = ((ComparatorSortTestCase) first)._id;
			int idB = ((ComparatorSortTestCase) second)._id;
			int modA = idA % 2;
			int modB = idB % 2;
			if (modA != modB) {
				return modA - modB;
			}
			return idA - idB;
		}
	}

	public static class AscendingNameComparator implements QueryComparator {
		public int compare(Object first, Object second) {
			return ((ComparatorSortTestCase) first)._name
					.compareTo(((ComparatorSortTestCase) second)._name);
		}
	}

	public static class SmallerThanThirtyPredicate extends
			Predicate<ComparatorSortTestCase> {
		public boolean match(ComparatorSortTestCase candidate) {
			return candidate._id < 30;
		}
	}

	public int _id;

	public String _name;

	public ComparatorSortTestCase() {
		this(0, null);
	}

	public ComparatorSortTestCase(int id, String name) {
		this._id = id;
		this._name = name;
	}

	protected void configure(Configuration config) {
		config.exceptionsOnNotStorable(true);
	}

	protected void store() {
		for (int i = 30; i >= 0; --i) {
			String name = i < 10 ? "0" + i : String.valueOf(i);
			store(new ComparatorSortTestCase(i, name));
		}
	}

	public void conc(ExtObjectContainer oc) {
		assertByIdAscending(oc);
		assertByIdAscendingConstrained(oc);
		assertByIdAscendingNQ(oc);
		
		assertByIdDescending(oc);
		asertByIdDescendingConstrained(oc);
		assertByIdDescendingNQ(oc);
		
		assertByIdOddEven(oc);
		assertByIdOddEvenConstrained(oc);
		assertByIdOddEvenNQ(oc);
		
		assertByNameAscending(oc);
		assertByNameAscendingConstrained(oc);
		assertByNameAscendingNQ(oc);
	}
	
	public void assertByIdAscending(ExtObjectContainer oc) {
		int[] expected = new int[31];
		for (int i = 0; i <= 30; ++i) {
			expected[i] = i;
		}
		assertIdOrder(oc,new AscendingIdComparator(), expected);
	}

	public void assertByIdAscendingConstrained(ExtObjectContainer oc) {
		int[] expected = new int[30];
		for (int i = 0; i < 30; ++i) {
			expected[i] = i;
		}
		Query query = oc.query();
		query.constrain(getClass());
		query.descend("_id").constrain(new Integer(30)).smaller();
		assertIdOrder(query, new AscendingIdComparator(), expected);
	}

	public void assertByIdAscendingNQ(ExtObjectContainer oc) {
		ObjectSet result = oc.query(new SmallerThanThirtyPredicate(),
				new AscendingIdComparator());
		int[] expected = new int[30];
		for (int i = 0; i < 30; ++i) {
			expected[i] = i;
		}
		assertIdOrder(result, expected);
	}
	

	public void assertByIdDescending(ExtObjectContainer oc) {
		int[] expected = new int[31];
		for (int i = 0; i <= 30; ++i) {
			expected[i] = 30-i;
		}
		assertIdOrder(oc, new DescendingIdComparator(), expected);
	}

	public void asertByIdDescendingConstrained(ExtObjectContainer oc) {
		int[] expected = new int[30];
		for (int i = 0; i <= 29; ++i) {
			expected[i] = 29-i;
		}
		Query query = oc.query();
		query.constrain(getClass());
		query.descend("_id").constrain(new Integer(30)).smaller();
		assertIdOrder(query, new DescendingIdComparator(), expected);
	}

	public void assertByIdDescendingNQ(ExtObjectContainer oc) {
		int[] expected = new int[30];
		for (int i = 0; i <= 29; ++i) {
			expected[i] = 29-i;
		}
		ObjectSet result = oc.query(new SmallerThanThirtyPredicate(),
				new DescendingIdComparator());
		assertIdOrder(result, expected);
	}
	
	public void assertByIdOddEven(ExtObjectContainer oc) {
		int[] expected = new int[31];
		int i = 0;
		for (; i <= 30/2; i++) {
			expected[i] = 2*i;
		}
		for (int j = 0; j <= (30-1)/2; j++) {
			expected[i++] = 2*j+1;
		}
		assertIdOrder(oc,new OddEvenIdComparator(), expected);
	}

	public void assertByIdOddEvenConstrained(ExtObjectContainer oc) {
		int[] expected = new int[30];
		int i = 0;
		for (; i < 30/2; i++) {
			expected[i] = 2*i;
		}
		for (int j = 0; j <= (30-1)/2; j++) {
			expected[i++] = 2*j+1;
		}
		Query query = oc.query();
		query.constrain(getClass());
		query.descend("_id").constrain(new Integer(30)).smaller();
		assertIdOrder(query, new OddEvenIdComparator(), expected);
	}

	public void assertByIdOddEvenNQ(ExtObjectContainer oc) {
		int[] expected = new int[30];
		int i = 0;
		for (; i < 30/2; i++) {
			expected[i] = 2*i;
		}
		for (int j = 0; j <= (30-1)/2; j++) {
			expected[i++] = 2*j+1;
		}
		ObjectSet result = oc.query(
				new SmallerThanThirtyPredicate(), new OddEvenIdComparator());
		assertIdOrder(result, expected);
	}
	
	public void assertByNameAscending(ExtObjectContainer oc) {
		int[] expected = new int[31];
		for (int i = 0; i <= 30; ++i) {
			expected[i] = i;
		}
		assertIdOrder(oc, new AscendingNameComparator(), expected);
	}

	public void assertByNameAscendingConstrained(ExtObjectContainer oc) {
		int[] expected = new int[30];
		for (int i = 0; i < 30; ++i) {
			expected[i] = i;
		}
		Query query = oc.query();
		query.constrain(getClass());
		query.descend("_id").constrain(new Integer(30)).smaller();
		assertIdOrder(query, new AscendingNameComparator(),expected);
	}

	public void assertByNameAscendingNQ(ExtObjectContainer oc) {
		int[] expected = new int[30];
		for (int i = 0; i < 30; ++i) {
			expected[i] = i;
		}
		ObjectSet result = oc.query(new SmallerThanThirtyPredicate(),new AscendingNameComparator());
		assertIdOrder(result, expected);
	}

	private void assertIdOrder(ExtObjectContainer oc,
			QueryComparator comparator, int[] ids) {
		Query query = oc.query();
		query.constrain(ComparatorSortTestCase.class);
		assertIdOrder(query, comparator, ids);
	}

	private void assertIdOrder(Query query, QueryComparator comparator,
			int[] ids) {
		query.sortBy(comparator);
		ObjectSet result = query.execute();
		assertIdOrder(result, ids);
	}

	private void assertIdOrder(ObjectSet result, int[] ids) {
		Assert.areEqual(ids.length, result.size());
		for (int idx = 0; idx < ids.length; idx++) {
			Assert.areEqual(ids[idx], ((ComparatorSortTestCase) result.next())._id);
		}
	}
}
