package com.db4o.osgi;

import org.osgi.framework.*;

import com.db4o.reflect.jdk.*;

public class OSGiLoader implements JdkLoader {
	
	private final Bundle _bundle;
	private JdkLoader _loader;
	
	public OSGiLoader(Bundle bundle, JdkLoader loader) {
		_bundle = bundle;
		_loader = loader;
	}

	public Class loadClass(String className) {
		try {
			return _bundle.loadClass(className);
		} 
		catch (ClassNotFoundException exc) {
			return _loader.loadClass(className);
		}
	}

	public Object deepClone(Object context) {
		return new OSGiLoader(_bundle, (JdkLoader) _loader.deepClone(context));
	}

}
