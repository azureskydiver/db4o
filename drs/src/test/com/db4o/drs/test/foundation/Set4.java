package com.db4o.drs.test.foundation;

import com.db4o.foundation.*;

public class Set4 implements Iterable4 {
	
	public static final Set4 EMPTY_SET = new Set4(0);
	
	private final Hashtable4 _table;
	
	public Set4() {
		_table = new Hashtable4();
	}
	
	public Set4(int size) {
		_table = new Hashtable4(size);
	}
	
	public Set4(Iterable4 keys) {
		this();
		addAll(keys);
	}

	public void add(Object element) {
		_table.put(element, element);
	}
	
	public void addAll(Iterable4 other) {
		Iterator4 i = other.iterator();
		while(i.moveNext()){
			add(i.current());			
		}
	}
	
	public boolean isEmpty() {
		return _table.size() == 0;
	}
	
	public int size() {
		return _table.size();
	}
	
	public boolean contains(Object element) {
		return _table.get(element) != null;
	}
	
	public boolean containsAll(Set4 other) {
		return _table.containsAllKeys(other);
	}
	
	public Iterator4 iterator() {
		return _table.keys();
	}
	
	public String toString() {
		StringBuffer buf=new StringBuffer("[");
		boolean first=true;
		for(Iterator4 iter=iterator();iter.moveNext();) {
			if(!first) {
				buf.append(',');
			}
			else {
				first=false;
			}
			buf.append(iter.current().toString());
		}
		buf.append(']');
		return buf.toString();
	}
}