package com.db4o.nativequery.expr.cmp;

public class MethodCallValue extends ComparisonOperandDescendant {
	private String _methodName;
	private Class[] _paramTypes; 
	private ComparisonOperand[] _params;
	
	public MethodCallValue(ComparisonOperandAnchor parent, String name, Class[] paramTypes, ComparisonOperand[] params) {
		super(parent);
		_methodName = name;
		_paramTypes = paramTypes;
		_params = params;
	}

	public void accept(ComparisonOperandVisitor visitor) {
		visitor.visit(this);
	}

	public String methodName() {
		return _methodName;
	}

	public Class[] paramTypes() {
		return _paramTypes;
	}

	public ComparisonOperand[] params() {
		return _params;
	}
	
	public boolean equals(Object obj) {
		if(!super.equals(obj)) {
			return false;
		}
		MethodCallValue casted=(MethodCallValue)obj;
		return _methodName.equals(casted._methodName)&&arrayCmp(_paramTypes, casted._paramTypes)&&arrayCmp(_params, casted._params);
	}

	public int hashCode() {
		int hc=super.hashCode();
		hc*=29+_methodName.hashCode();
		hc*=29+_paramTypes.hashCode();
		hc*=29+_params.hashCode();
		return hc;
	}
	
	public String toString() {
		String str=super.toString()+"."+_methodName+"(";
		for (int paramIdx = 0; paramIdx < _paramTypes.length; paramIdx++) {
			if(paramIdx>0) {
				str+=",";
			}
			str+=_paramTypes[paramIdx]+":"+_params[paramIdx];
		}
		str+=")";
		return str;
	}
	
	private boolean arrayCmp(Object[] a,Object[] b) {
		if(a.length!=b.length) {
			return false;
		}
		for (int paramIdx = 0; paramIdx < a.length; paramIdx++) {
			if(!a[paramIdx].equals(b[paramIdx])) {
				return false;
			}
		}
		return true;
	}
}
