package com.db4o.nativequery.expr.cmp.field;

import com.db4o.nativequery.expr.cmp.*;

public class PredicateFieldRoot extends ComparisonOperandRoot {
	public final static PredicateFieldRoot INSTANCE=new PredicateFieldRoot();
	
	private PredicateFieldRoot() {}

	public String toString() {
		return "PREDICATE";
	}

	public void accept(ComparisonOperandVisitor visitor) {
		visitor.visit(this);
	}
}
