package com.db4o.browser.model.test;

import com.db4o.*;
import com.db4o.ext.*;

public class MockObjectSet implements ObjectSet {
	public MockObjectSet(Object[] data) {
	}

	public ExtObjectSet ext() {
		return null;
	}

	public boolean hasNext() {
		return false;
	}

	public Object next() {
		return null;
	}

	public void reset() {
	}

	public int size() {
		return 0;
	}

}
