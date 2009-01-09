/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import com.db4o.*;
import com.db4o.collections.*;
import com.db4o.config.*;
import com.db4o.foundation.io.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.fixtures.*;

/**
 * @sharpen.ignore
 */
@decaf.Ignore
public class BigSetPerformanceMain extends FixtureTestSuiteDescription {
	
	{
		fixtureProviders(new SubjectFixtureProvider(new Object[] { 10, 100, 1000, 10000 }));
		testUnits(BigSetPerformance.class);
    }
	
	public static void main(String[] args) {
		new Db4oTestSuite() {

			@Override
            protected Class[] testCases() {
				return new Class[] { BigSetPerformanceMain.class };
            }
			
		}.runSolo();
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
			_container = openFile();
			System.out.println("Element count: " + count());
			System.out.println("Add runs: " + ADD_RUNS);
		}

		private ObjectContainer openFile() {
	        final Configuration config = Db4o.newConfiguration();
			config.bTreeNodeSize(1000);
			return Db4o.openFile(config, FILENAME);
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
				storeAndCommit(list);
			}
			long stop = System.currentTimeMillis();
			long duration = stop - start;
			System.out.println("ArrayList single add: " + duration + "ms");

		}

		private void storeAndCommit(Object o) {
	        _container.store(o);
	        _container.commit();
        }

		private List timePlainListCreation() {
			long start = System.currentTimeMillis();
			storeAndCommit(setUpList());
			long stop = System.currentTimeMillis();
			long duration = stop - start;
			System.out.println("ArrayList creation: " + duration + "ms");
			return setUpList();
		}

		private List setUpList() {
	        List list = new ArrayList();
			for (int i = 0; i < count(); i++) {
				list.add(new Item(i));
			}
	        return list;
        }

		private Set timeBigSetCreation() {
			long start = System.currentTimeMillis();
			storeAndCommit(setUpSet());
			long stop = System.currentTimeMillis();
			long duration = stop - start;
			System.out.println("Big Set creation: " + duration + "ms");
			return setUpSet();
		}

		private Set setUpSet() {
	        Set set = newBigSet();
			for (int i = 0; i < count(); i++) {
				set.add(new Item(i));
			}
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
				storeAndCommit(set);
			}
			long stop = System.currentTimeMillis();
			long duration = stop - start;
			System.out.println("BigSet single add: " + duration + "ms");
		}
	}	
}
