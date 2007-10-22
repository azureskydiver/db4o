package com.db4o.db4ounit.common.activation;

import com.db4o.foundation.*;
import db4ounit.*;

public class MethodCall {
	public final String methodName;
	public final Object[] args;
	
	public MethodCall(String methodName, Object[] args) {
		this.methodName = methodName;
		this.args = args;
	}
	
	public MethodCall(String methodName, Object singleArg) {
		this(methodName, new Object[] { singleArg });
	}
	
	public String toString() {
		return methodName + "(" + Iterators.join(Iterators.iterate(args), ", ") + ")";
	}
	
	public boolean equals(Object obj) {
		if (null == obj) return false;
		if (getClass() != obj.getClass()) return false;
		MethodCall other = (MethodCall)obj;
		if (!methodName.equals(other.methodName)) return false;
		if (args.length != other.args.length) return false;
		for (int i=0; i<args.length; ++i) {
			if (!Check.objectsAreEqual(args[i], other.args[i])) {
				return false;
			}
		}
		return true;
	}
}
