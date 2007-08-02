/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.ta.tests.collections.internal;

import com.db4o.ta.tests.*;

public class Page extends ActivatableImpl {

	public static final int PAGESIZE = 100;
	
	private Object[] _data = new Object[PAGESIZE];
	private int _top = 0;

	private int _pageIndex;

	private transient boolean _dirty = false;

	public Page(int pageIndex) {
		_pageIndex = pageIndex;
	}

	public boolean add(Object obj) {
		// TA BEGIN
		activate();
		// TA END
		_dirty = true;
		_data[_top++] = obj;
		return true;
	}

	public int size() {
		// TA BEGIN
		activate();
		// TA END
		return _top;
	}

	public Object get(int indexInPage) {
		// TA BEGIN
		activate();
		// TA END
//		System.out.println("got from page: " + _pageIndex);
		_dirty = true; // just to be safe, we'll mark things as dirty if they are used.
		return _data[indexInPage];
	}

	public boolean isDirty() {
		// TA BEGIN
//		activate();
		// TA END
		return _dirty;
	}

	public void setDirty(boolean dirty) {
		// TA BEGIN
//		activate();
		// TA END
		_dirty = dirty;
	}

	public int getPageIndex() {
		// TA BEGIN
		activate();
		// TA END
		return _pageIndex;
	}

	public boolean atCapacity() {
		return capacity() == 0;
	}
	
	public int capacity() {
		// TA BEGIN
		activate();
		// TA END
		return Page.PAGESIZE - size();
	}
}
