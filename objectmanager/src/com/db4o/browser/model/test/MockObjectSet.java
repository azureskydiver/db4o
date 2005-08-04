package com.db4o.browser.model.test;

import java.util.ArrayList;

import com.db4o.ObjectSet;
import com.db4o.ext.ExtObjectSet;

public class MockObjectSet extends ArrayList implements ObjectSet {
	private Object[] data;
	private int idx;
	
	public MockObjectSet(Object[] data) {
		this.data=data;
		this.idx=0;
	}

	public ExtObjectSet ext() {
		throw new UnsupportedOperationException("TODO: implement");
	}

	public boolean hasNext() {
		return idx<data.length;
	}

	public Object next() {
		return data[idx++];
	}

	public void reset() {
		idx=0;
	}

	public int size() {
		return data.length;
	}

	public void remove() {
		// TODO Auto-generated method stub
		
	}

}
