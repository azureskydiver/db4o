package com.db4o.ta.instrumentation.test;

public class ToBeInstrumentedOuter {
	
	public int _foo;
	
	public int foo() {
		return _foo;
	}
	
	public class ToBeInstrumentedInner{
		
		public ToBeInstrumentedOuter getOuterObject(){
			return ToBeInstrumentedOuter.this;
		}
	} 

}
