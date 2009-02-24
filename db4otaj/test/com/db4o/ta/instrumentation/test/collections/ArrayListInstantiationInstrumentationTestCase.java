package com.db4o.ta.instrumentation.test.collections;

import java.util.*;

import com.db4o.collections.*;
import com.db4o.internal.*;
import com.db4o.ta.instrumentation.*;
import com.db4o.ta.instrumentation.test.*;

import db4ounit.*;

public class ArrayListInstantiationInstrumentationTestCase implements TestCase {

	public void testConstructorIsExchanged() throws Exception {
		Class instrumented = instrument(ArrayListHolder.class);
		Object instance = instrumented.newInstance();
		assertReturnsActivatableList(instance, "createArrayList");
		assertReturnsActivatableList(instance, "createSizedArrayList");
		assertReturnsActivatableList(instance, "createNestedArrayList");
		assertReturnsActivatableList(instance, "createMethodArgArrayList");
		assertReturnsActivatableList(instance, "createConditionalArrayList");
	}

	public void testCustomArrayList() throws Exception {
		Class instrumented = instrument(MyArrayList.class);
		List list = (List)instrumented.newInstance();
		List delegateList = (List)instrumented.getField("_delegate").get(list);
		Assert.isInstanceOf(ActivatableArrayList.class, delegateList);
	}
	
	private void assertReturnsActivatableList(Object instance, String methodName) {
		List list = (List)Reflection4.invoke(instance, methodName);
		Assert.isInstanceOf(ActivatableArrayList.class, list);
	}

	private Class instrument(Class clazz) throws ClassNotFoundException {
		return InstrumentationEnvironment.enhance(clazz, new ArrayListInstantiationEdit());
	}
}
