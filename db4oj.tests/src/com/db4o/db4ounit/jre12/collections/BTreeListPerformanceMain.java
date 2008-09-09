/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import com.db4o.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;
import com.db4o.internal.collections.*;

import db4ounit.*;

/**
 * @exclude
 */
public class BTreeListPerformanceMain implements TestLifeCycle{
	
	
	
	private static final int COUNT = 1000000;
	
	private static final int ADD_RUNS = 5;

		
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
	
	private void timePlainListSingleAdd(List list) {
		long start = System.currentTimeMillis();
		for (int i = 0; i < ADD_RUNS; i++) {
			list.add(i);
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
			list.add(i);
		}
		_container.store(list);
		_container.commit();
		long stop = System.currentTimeMillis();
		long duration = stop - start;
		System.out.println("ArrayList creation: " + duration + "ms" );
		return list;
	}

	public void testTimeBTreeList(){
		BTreeList btreeList = timeBTreeListCreation();
		timeBTreeListSingleAdd(btreeList);
	}

	private void timeBTreeListSingleAdd(BTreeList btreeList) {
		Transaction trans = trans();
		long start = System.currentTimeMillis();
		for (int i = 0; i < ADD_RUNS; i++) {
			btreeList.add(trans, i);
			btreeList.commit(trans);
			_container.commit();
		}
		long stop = System.currentTimeMillis();
		long duration = stop - start;
		System.out.println("BTreeList single add: " + duration + "ms" );
		
	}

	private BTreeList timeBTreeListCreation() {
		long start = System.currentTimeMillis();
		Transaction trans = trans();
		BTreeList<Integer> list = new BTreeList(trans);
		for (int i = 0; i < COUNT; i++) {
			list.add(trans, i);
		}
		list.commit(trans);
		_container.commit();
		long stop = System.currentTimeMillis();
		long duration = stop - start;
		System.out.println("BTreeList creation: " + duration + "ms" );
		return list;
	}

	private Transaction trans() {
		return ((PartialObjectContainer)_container).transaction();
	}
	
	


	
	

}
