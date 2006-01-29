package com.db4o.nativequery.expr.cmp;

import com.db4o.nativequery.expr.cmp.field.*;

public interface FieldRootVisitor {
	void visit(PredicateFieldRoot root);
	void visit(CandidateFieldRoot root);
	void visit(StaticFieldRoot root);
}
