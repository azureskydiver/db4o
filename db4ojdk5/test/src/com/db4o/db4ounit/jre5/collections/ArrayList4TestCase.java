/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections;

import java.util.*;

import com.db4o.collections.*;
import com.db4o.db4ounit.common.ta.*;
import com.db4o.internal.*;

import db4ounit.*;

public class ArrayList4TestCase implements TestLifeCycle {

	public static void main(String[] args) {
		new TestRunner(ArrayList4TestCase.class).run();
	}
	
	public ArrayList4 <Integer> _list;

	public void setUp() throws Exception {
		_list = new ArrayList4<Integer>();
		ListAsserter.createList(_list);
	}

	public void tearDown() throws Exception {
		
	}
	
    public void testCloneWontCopyActivator() throws Exception {
    	_list.bind(new MockActivator());
		
		final Object clone = _list.clone();
		Assert.isNull(Reflection4.getFieldValue(clone, "_activator"));
    }
	
	public void testConstructor() throws Exception {
		ArrayList4<Integer> arrayList = new ArrayList4<Integer>();
		fill(arrayList);
		Assert.areEqual(ListAsserter.CAPACITY, arrayList.size());
	}
	
	public void testConstructor_I_LegalArguments1() throws Exception {
		ArrayList4<Integer> arrayList; 
		arrayList = new ArrayList4<Integer>(ListAsserter.CAPACITY);
		fill(arrayList);
		Assert.areEqual(ListAsserter.CAPACITY, arrayList.size());
	}
	
	public void testConstructor_I_LegalArguments2() throws Exception {
		ArrayList4<Integer> arrayList; 
		arrayList = new ArrayList4<Integer>(0);
		fill(arrayList);
		Assert.areEqual(ListAsserter.CAPACITY, arrayList.size());
	}

	public void testConstructor_I_IllegalArgumentException() throws Exception {
		Assert.expect(IllegalArgumentException.class, new CodeBlock(){
			public void run() throws Throwable {
				new ArrayList4<Integer>(-1);
			}
		});
	}
	
	public void testConstructor_LCollection_NullPointerException() throws Exception {
		Assert.expect(NullPointerException.class, new CodeBlock(){
			public void run() throws Throwable {
				new ArrayList4<Integer>(null);
			}
		});
	}
	
	public void testConstructor_LCollection() throws Exception {
		ArrayList4<Integer> arrayList = new ArrayList4<Integer>(_list);
		Assert.areEqual(_list.size(), arrayList.size());
		Assert.isTrue(Arrays.equals(_list.toArray(), arrayList.toArray()));
		
	}

	private void fill(ArrayList4<Integer> arrayList) {
		for (int i = 0; i < ListAsserter.CAPACITY; i++) {
			arrayList.add(new Integer(i));
		}
	}

	public void testAdd() throws Exception {
		ListAsserter.assertAdd(_list);
	}

	public void testAdd_LObject() throws Exception {
		ListAsserter.assertAdd_LObject(_list);
	}

	public void testAddAll_LCollection() throws Exception {
		ListAsserter.assertAddAll_LCollection(_list);
	}

	public void testClear() throws Exception {
		ListAsserter.assertClear(_list);
	}

	public void testContains() throws Exception {
		ListAsserter.assertContains(_list);
	}

	public void testContainsAll() throws Exception {
		ListAsserter.assertContainsAll(_list);
	}

	public void testIndexOf() throws Exception {
		ListAsserter.assertIndexOf(_list);
	}

	public void testIsEmpty() throws Exception {
		ListAsserter.assertIsEmpty(_list);
		Assert.isTrue(new ArrayList4<Integer>().isEmpty());
	}

	public void testIterator() throws Exception {
		ListAsserter.assertIterator(_list);
	}

	public void testLastIndexOf() throws Exception {
		ListAsserter.assertLastIndexOf(_list);
	}

	public void testRemove_LObject() throws Exception {
		ListAsserter.assertRemove_LObject(_list);
	}

	public void testRemoveAll() throws Exception {
		ListAsserter.assertRemoveAll(_list);
	}

	public void testSet() throws Exception {
		ListAsserter.assertSet(_list);
	}

	public void testSize() throws Exception {
		ListAsserter.assertSize(_list);
	}
	
	public void testToArray() throws Exception {
		ListAsserter.assertToArray(_list);
	}
	
	public void testToArray_LObject() throws Exception {
		ListAsserter.assertToArray_LObject(_list);
	}
	
	public void testToString() throws Exception {
		ListAsserter.assertToString(_list);
        ArrayList4<Object> oList = new ArrayList4<Object>();    
        Assert.areEqual("[]",oList.toString());
        
        oList.add(new Integer(1));
        oList.add(new Integer(2));
        Assert.areEqual("[1, 2]",oList.toString());
        
        oList.add(oList);
        oList.add(new Integer(3));
        Assert.areEqual("[1, 2, (this Collection), 3]",oList.toString());
	}
	
	public void testTrimToSize_EnsureCapacity() throws Exception {
		ArrayList4Asserter.assertTrimToSize_EnsureCapacity(_list);
	}
	
	public void testTrimToSize_Remove() throws Exception {
		ListAsserter.assertTrimToSize_Remove(_list);
	}
	
	public void testTrimToSize_Iterator() throws Exception {
		ArrayList4Asserter.assertTrimToSize_Iterator(_list);
	}
	
	public void testEnsureCapacity_Iterator() throws Exception {
		ArrayList4Asserter.assertEnsureCapacity_Iterator(_list);
	}
	
	public void testClear_Iterator() throws Exception {
		ListAsserter.assertClear_Iterator(_list);
	}
	
	public void testClone() throws Exception {
		ListAsserter.assertClone(_list);
	}
	
	public void testEquals() throws Exception {
		ListAsserter.assertEquals(_list);
	}
	
	public void testIteratorNext_NoSuchElementException() throws Exception {
		ListAsserter.assertIteratorNext_NoSuchElementException(_list);
	}
	
	public void testIteratorNext_ConcurrentModificationException() throws Exception {
		ListAsserter.assertIteratorNext_ConcurrentModificationException(_list);
	}
	
	public void testIteratorNext() throws Exception {
		ListAsserter.assertIteratorNext(_list);
	}
	
	public void testIteratorRemove() throws Exception {
		ListAsserter.assertIteratorRemove(_list);
	}
	
	public void testRemove_IllegalStateException() throws Exception {
		ListAsserter.assertRemove_IllegalStateException(_list);
	}
	
	public void testIteratorRemove_ConcurrentModificationException() throws Exception {
		ListAsserter.assertIteratorRemove_ConcurrentModificationException(_list);
	}
	
	public void testSubList() throws Exception {
		ListAsserter.assertSubList(_list);
	}
	
	public void testSubList_ConcurrentModification() throws Exception {
		ListAsserter.assertSubList_ConcurrentModification(_list);
	}
	
	public void testSubList_Clear() throws Exception {
		ListAsserter.assertSubList_Clear(_list);
	}
	
	public void testSubList_Clear2() throws Exception {
		ListAsserter.assertSubList_Clear2(_list);
	}
	
}
