/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import com.db4o.*;
import com.db4o.collections.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.fixtures.*;

/**
 * @decaf.ignore
 * @sharpen.ignore
 */
public class BigSetPerformanceMain extends FixtureBasedTestSuite {
	
	@Override
    public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[] {
			new SubjectFixtureProvider(new Object[] { new Integer(10), new Integer(100), new Integer(1000), new Integer(10000) })
		};
    }

	@Override
    public Class[] testUnits() {
		return new Class[] { BigSetPerformance.class };
    }

	public static class BigSetPerformance implements TestLifeCycle {

		private static final int ADD_RUNS = 50;

		public static class Item {

			public int _value;

			public Item(int value) {
				_value = value;
			}
		}

		private ObjectContainer _container;

		static final String FILENAME = Path4.getTempFileName();

		public void setUp() throws Exception {
			Db4o.configure().bTreeNodeSize(1000);
			_container = Db4o.openFile(FILENAME);
			System.out.println("Element count: " + count());
			System.out.println("Add runs: " + ADD_RUNS);
		}

		public void tearDown() {
			_container.close();
			File4.delete(FILENAME);
		}

		public void testTimePlainList() {
			List list = timePlainListCreation();
			timePlainListSingleAdd(list);
		}

		public void testTimeBigSet() {
			Set set = timeBigSetCreation();
			timeBigSetSingleAdd(set);
		}

		private void timePlainListSingleAdd(List list) {
			long start = System.currentTimeMillis();
			for (int i = 0; i < ADD_RUNS; i++) {
				list.add(new Item(i));
				_container.store(list);
				_container.commit();
			}
			long stop = System.currentTimeMillis();
			long duration = stop - start;
			System.out.println("ArrayList single add: " + duration + "ms");

		}

		private List timePlainListCreation() {
			long start = System.currentTimeMillis();
			List list = new ArrayList();
			for (int i = 0; i < count(); i++) {
				list.add(new Item(i));
			}
			_container.store(list);
			_container.commit();
			long stop = System.currentTimeMillis();
			long duration = stop - start;
			System.out.println("ArrayList creation: " + duration + "ms");
			return list;
		}

		private Set timeBigSetCreation() {
			long start = System.currentTimeMillis();
			Set set = newBigSet();
			for (int i = 0; i < count(); i++) {
				set.add(new Item(i));
			}
			_container.store(set);
			_container.commit();
			long stop = System.currentTimeMillis();
			long duration = stop - start;
			System.out.println("Big Set creation: " + duration + "ms");
			return set;
		}

		private int count() {
			return ((Integer) SubjectFixtureProvider.value()).intValue();
        }

		private Set newBigSet() {
			return CollectionFactory.forObjectContainer(_container).newBigSet();
		}

		private void timeBigSetSingleAdd(Set set) {
			long start = System.currentTimeMillis();
			for (int i = 0; i < ADD_RUNS; i++) {
				set.add(new Item(i));
				_container.store(set);
				_container.commit();
			}
			long stop = System.currentTimeMillis();
			long duration = stop - start;
			System.out.println("BigSet single add: " + duration + "ms");
		}

		private Transaction trans() {
			return ((ObjectContainerBase) _container).transaction();
		}
	}

	
}
