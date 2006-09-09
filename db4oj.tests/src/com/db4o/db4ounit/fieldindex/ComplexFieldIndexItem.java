package com.db4o.db4ounit.fieldindex;

public class ComplexFieldIndexItem implements HasFoo {
	public int foo;
	public int bar;
	public ComplexFieldIndexItem child;
	
	public ComplexFieldIndexItem() {
	}
	
	public ComplexFieldIndexItem(int foo_, int bar_, ComplexFieldIndexItem child_) {
		foo = foo_;
		bar = bar_;
		child = child_;
	}
	
	public int getFoo() {
		return foo;
	}
}
