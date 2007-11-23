/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.reflect.jdk;

import com.db4o.foundation.*;

/**
 * @exclude
 * 
 * @sharpen.ignore
 */
public interface JdkLoader extends DeepClone {
	Class loadClass(String className);
}
