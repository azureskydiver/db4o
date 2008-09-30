/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import com.db4o.*;
import com.db4o.collections.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;

import db4ounit.*;

/**
 * @decaf.ignore
 * @sharpen.ignore
 */
public class BigSetPerformanceMain implements TestLifeCycle{
	
	
	private static final int COUNT = 1000;
	
	private static final int ADD_RUNS = 5;
	
	public static class Item {
		
		public int _value;
		
		public Item(int value){
			_value = value;
		}
		
	}

		
	private ObjectContainer _container;
	
	static final String FILENAME = Path4.getTempFileName();
	
	public void setUp() throws Exception {
		Db4o.configure().bTreeNodeSize(1000);
		_container = Db4o.openFile(FILENAME);
		System.out.println("Element count: " + COUNT);
		System.out.println("Add runs: " + ADD_RUNS);
	}
	
	public void tearDown(){
		_container.close();
		File4.delete(FILENAME);
	}
	
	public void testTimePlainList(){
		List list = timePlainListCreation();
		timePlainListSingleAdd(list);
	}
	
	public void testTimeBigSet(){
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
		System.out.println("ArrayList single add: " + duration + "ms" );
		
	}

	private List timePlainListCreation() {
		long start = System.currentTimeMillis();
		List list = new ArrayList();
		for (int i = 0; i < COUNT; i++) {
			list.add(new Item(i));
		}
		_container.store(list);
		_container.commit();
		long stop = System.currentTimeMillis();
		long duration = stop - start;
		System.out.println("ArrayList creation: " + duration + "ms" );
		return list;
	}
	
	private Set timeBigSetCreation() {
		long start = System.currentTimeMillis();
		Set set = new BigSet(_container);
		for (int i = 0; i < COUNT; i++) {
			set.add(new Item(i));
		}
		_container.store(set);
		_container.commit();
		long stop = System.currentTimeMillis();
		long duration = stop - start;
		System.out.println("Big Set creation: " + duration + "ms" );
		return set;
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
		System.out.println("BigSet single add: " + duration + "ms" );
	}

	private Transaction trans() {
		return ((ObjectContainerBase)_container).transaction();
	}
	
	


	
	

}
