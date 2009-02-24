package com.db4o.ta.instrumentation.test.collections;

import java.util.*;

public class MyArrayList extends ArrayList {

	public List _delegate;
	
	public MyArrayList() {
		super();
		_delegate = new ArrayList();
	}
	
}
