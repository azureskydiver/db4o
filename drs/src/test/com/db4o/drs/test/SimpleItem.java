package com.db4o.drs.test;

public class SimpleItem {
	private String value;
	private SimpleItem child;
	private SimpleListHolder parent;
	
	public SimpleItem() {
	}

	public SimpleItem(String value_) {
		this(null, value_);
	}
	
	public SimpleItem(SimpleListHolder parent_, String value_) {
		this(parent_, value_, null);
	}

	public SimpleItem(SimpleListHolder parent_, String value_, SimpleItem child_) {
		parent = parent_;
		value = value_;
		child = child_;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value_) {
		value = value_;
	}

	public SimpleItem getChild() {
		return getChild(0);
	}
	
	public SimpleItem getChild(int level) {
		SimpleItem tbr = child;
		while (--level > 0 && tbr != null) {
			tbr = tbr.child;
		}
			
		return tbr;
	}	

	public void setChild(SimpleItem child_) {
		child = child_;
	}

	public SimpleListHolder getParent() {
		return parent;
	}

	public void setParent(SimpleListHolder parent_) {
		parent = parent_;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() != SimpleItem.class) {
			return false;
		}
		
		SimpleItem rhs = (SimpleItem) obj;
		return rhs.getValue().equals(getValue());
	}
	
	@Override
	public String toString() {
		String childString;
		
		if (child != null) {
			childString = child != this ? child.toString() : "this";
		}
		else {
			childString = "null";
		}
		
		return value + "[" + childString + "]";
	}
}
