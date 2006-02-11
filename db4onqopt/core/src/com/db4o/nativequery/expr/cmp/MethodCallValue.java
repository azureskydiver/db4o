package com.db4o.nativequery.expr.cmp;

public class MethodCallValue extends ComparisonOperandDescendant {
	private String _methodName;
	private String[] _paramTypeNames; 
	private ComparisonOperand[] _params;
	
	public MethodCallValue(ComparisonOperandAnchor parent, String name, String[] typeNames, ComparisonOperand[] params) {
		super(parent);
		_methodName = name;
		_paramTypeNames = typeNames;
		_params = params;
	}

	public void accept(ComparisonOperandVisitor visitor) {
		visitor.visit(this);
	}

	public String methodName() {
		return _methodName;
	}

	public String[] paramTypeNames() {
		return _paramTypeNames;
	}

	public ComparisonOperand[] params() {
		return _params;
	}
}
