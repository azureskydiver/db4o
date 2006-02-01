package com.db4o.nativequery.expr.cmp.field;

import com.db4o.nativequery.expr.cmp.*;

public class CandidateFieldRoot implements ComparisonOperand {
	public final static CandidateFieldRoot INSTANCE=new CandidateFieldRoot();
	
	private CandidateFieldRoot() {}
	
	public String toString() {
		return "CANDIDATE";
	}

	public void accept(ComparisonOperandVisitor visitor) {
		visitor.visit(this);
	}
}
