package com.db4o.ta.instrumentation.test;

public class ToBeInstrumentedWithExternalFieldAccess {

	public void accessExternalField(ToBeInstrumentedWithFieldAccess other) {
		other._externallyAccessibleInt = 42;
	}
}
