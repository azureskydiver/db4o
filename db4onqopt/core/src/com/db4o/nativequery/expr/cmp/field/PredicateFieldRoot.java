package com.db4o.nativequery.expr.cmp.field;

import com.db4o.nativequery.expr.cmp.*;

public class PredicateFieldRoot implements FieldRoot {
	public final static PredicateFieldRoot INSTANCE=new PredicateFieldRoot();
	
	private PredicateFieldRoot() {}

	public void accept(FieldRootVisitor visitor) {
		visitor.visit(this);
	}
	
	public String toString() {
		return "PREDICATE";
	}
}
