package com.db4o.ta.instrumentation.test.collections;

import java.util.*;

public class ArrayListHolder {

	public List createArrayList() {
		return new ArrayList();
	}

	public List createSizedArrayList() {
		return new ArrayList(42);
	}

}
