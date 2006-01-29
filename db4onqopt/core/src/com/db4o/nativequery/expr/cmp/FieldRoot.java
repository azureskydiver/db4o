package com.db4o.nativequery.expr.cmp;

public interface FieldRoot {
	void accept(FieldRootVisitor visitor);
}
