package com.db4o.drs.test;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class SimpleListHolder {
	
	private String _name;
	
	public SimpleListHolder(String name){
		_name = name;
	}
	
	private List list = new ArrayList();

	public List list() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}
	
	public void add(SimpleItem item) {
		list.add(item);
	}

	public void name(String name) {
		_name = name;
	}
}
