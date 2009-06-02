/* Copyright (C) 2009  Versant Corp.  http://www.db4o.com */

package com.db4o.ta.instrumentation.test;

import java.net.*;

import com.db4o.instrumentation.classfilter.*;
import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.main.*;
import com.db4o.ta.*;
import com.db4o.ta.instrumentation.*;

import db4ounit.*;

@decaf.Remove
public class EnumTestCase implements TestLifeCycle{
	
	private BloatInstrumentingClassLoader _loader;
	
	public static enum MyEnum {
		
	}
	
	public void setUp() throws Exception {
		ClassLoader baseLoader = MyEnum.class.getClassLoader();
		ClassFilter filter = new ByNameClassFilter(new String[]{ enumClassName() });
		_loader = new BloatInstrumentingClassLoader(new URL[] {}, baseLoader, new AcceptAllClassesFilter(), new InjectTransparentActivationEdit(filter));
	}

	private String enumClassName() {
		return MyEnum.class.getName();
	}
	
	public void testEnumIsNotActivatable() throws ClassNotFoundException{
		Class<?> enumClass = _loader.loadClass(enumClassName());
		Assert.isFalse(Activatable.class.isAssignableFrom(enumClass));
	}

	public void tearDown() throws Exception {
		
	}


}
