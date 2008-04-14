package com.db4o.drs.test;

@SuppressWarnings("unchecked")
public class Item extends ListContent {
	private final ListHolder _parent;
	private Item _child;
	
	public Item(String name) {
		this(name, null, null);
	}
	
	public Item(String name, Item child, ListHolder parent) {
		super(name);
		_parent = parent;
		_child = child;
	}

	public Item(String name, Item child) {
		this(name, child, null);
	}

	public Item(String name, ListHolder list) {
		this(name, null, list);
	}
	
	public Item child() {
		return child(1);
	}
	
	public Item child(int level) {
		Item tbr = _child;
		while (--level > 0 && tbr != null) {
			tbr = tbr._child;
		}
			
		return tbr;
	}
	
	public ListHolder parent() {
		return _parent;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() != Item.class) {
			return false;
		}
		
		Item rhs = (Item) obj;
		return rhs.getName().equals(getName());
	}

	public void setChild(Item child) {
		_child = child;
	}
}
