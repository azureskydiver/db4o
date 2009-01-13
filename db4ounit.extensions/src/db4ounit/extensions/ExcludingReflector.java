/* Copyright (C) 2009   db4objects Inc.   http://www.db4o.com */
package db4ounit.extensions;

import com.db4o.foundation.*;
import com.db4o.reflect.*;
import com.db4o.reflect.jdk.*;

/**
 * @sharpen.extends Db4objects.Db4o.Reflect.Net.NetReflector
 */
public class ExcludingReflector extends JdkReflector {

	private final Collection4 _excludedClasses;
	
	/**
	 * @sharpen.remove.first
	 */
	public ExcludingReflector(Class<?>... excludedClasses) {
		super(ExcludingReflector.class.getClassLoader());
		
		_excludedClasses = new Collection4();
		for(Class<?> claxx : excludedClasses) {
			_excludedClasses.add(claxx.getName());
		}
	}
	
	@Override
	public ReflectClass forName(String className) {
		if (_excludedClasses.contains(className)) {
			return null;
		}
		return super.forName(className);
	}
}
