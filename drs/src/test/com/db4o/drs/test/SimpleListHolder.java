package com.db4o.drs.test;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class SimpleListHolder {
	private List list = new ArrayList();

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}
	
	public void add(SimpleItem item) {
		list.add(item);
	}
}
