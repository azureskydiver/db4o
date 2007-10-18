package com.db4o.ta.instrumentation.test;

import java.lang.reflect.*;
import java.net.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.filter.*;
import com.db4o.instrumentation.main.*;
import com.db4o.reflect.jdk.*;
import com.db4o.ta.*;
import com.db4o.ta.instrumentation.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class NestedClassesTestCase extends AbstractDb4oTestCase {
	
	private ClassLoader _classLoader;
	private Object _outerObject;
	private Class _outerClazz;

	public static void main(String[] args) {
		new NestedClassesTestCase().runSolo();
	}
	
	protected void store() throws Exception {
		_outerClazz = _classLoader.loadClass(ToBeInstrumentedOuter.class.getName());
		_outerObject = _outerClazz.newInstance();
		_outerClazz.getField("_foo").set(_outerObject, new Integer(101));
		
		final Activatable objOne = createInnerObject(_outerClazz, _outerObject);
		store(objOne);
		
		final Activatable objTwo = createInnerObject(_outerClazz, _outerObject);
		store(objTwo);
	}

	private Activatable createInnerObject(Class outerClazz,
			Object outerObj) throws Exception {
		
		Class innerClazz = _classLoader.loadClass(ToBeInstrumentedOuter.ToBeInstrumentedInner.class.getName());
		final Activatable objOne = (Activatable) 
		innerClazz.getConstructor(
				new Class[] {outerClazz}
				).newInstance(new Object[]{outerObj});
		return objOne;
	}
	
	protected void configure(Configuration config) throws Exception {
		ClassLoader baseLoader = NestedClassesTestCase.class.getClassLoader();
		URL[] urls = {};
		ClassFilter filter = new ByNameClassFilter(new String[] { ToBeInstrumentedOuter.class.getName(), ToBeInstrumentedOuter.ToBeInstrumentedInner.class.getName() });
		BloatClassEdit edit = new InjectTransparentActivationEdit(filter);
		_classLoader = new BloatInstrumentingClassLoader(urls, baseLoader, filter, edit);
		config.add(new TransparentActivationSupport());
		config.activationDepth(0);
		config.reflectWith(new JdkReflector(_classLoader));
	}
	
	public void test() throws Exception {
		ObjectSet query = db().query(ToBeInstrumentedOuter.ToBeInstrumentedInner.class);
		while(query.hasNext()){
			Object innerObject = query.next();
			Method outerObjectMethod = innerObject.getClass().getDeclaredMethod("getOuterObject", new Class[]{});
			Object outerObject = outerObjectMethod.invoke(innerObject, new Object[]{});
			Method fooMehtod = _outerClazz.getDeclaredMethod("foo", new Class[]{});
			Assert.areEqual(fooMehtod.invoke(_outerObject, new Object[]{}), fooMehtod.invoke(outerObject, new Object[]{}));
		}
	}
	
}
