package com.db4o.ta.instrumentation.test.data;

import java.util.*;

public class ArrayListClient {

	private List _list;
	
	public ArrayListClient() {
		_list = new ArrayList();
	}

	public List list() {
		return _list;
	}
}
