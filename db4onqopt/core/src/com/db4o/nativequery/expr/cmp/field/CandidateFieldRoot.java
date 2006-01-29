package com.db4o.nativequery.expr.cmp.field;

import com.db4o.nativequery.expr.cmp.*;

public class CandidateFieldRoot implements FieldRoot {
	public final static CandidateFieldRoot INSTANCE=new CandidateFieldRoot();
	
	private CandidateFieldRoot() {}
	
	public void accept(FieldRootVisitor visitor) {
		visitor.visit(this);
	}
	
	public String toString() {
		return "CANDIDATE";
	}
}
