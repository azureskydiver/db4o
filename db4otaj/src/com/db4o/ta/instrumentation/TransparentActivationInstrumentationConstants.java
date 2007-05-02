package com.db4o.ta.instrumentation;

public abstract class TransparentActivationInstrumentationConstants {

	public final static String ACTIVATOR_FIELD_NAME = "_db4o$$ta$$activator";
	public final static String BIND_METHOD_NAME = "bind";
	public static final String INIT_METHOD_NAME = "<init>";
	public static final String ASSERT_COMPATIBLE_METHOD_NAME = "assertCompatible";
	public static final String ACTIVATE_METHOD_NAME = "db4o$$ta$$activate";
	public static final String ACTIVATOR_ACTIVATE_METHOD_NAME = "activate";
	
	private TransparentActivationInstrumentationConstants() {}
}
