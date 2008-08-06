/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.reflect.*;
import com.db4o.typehandlers.*;


/**
 * @exclude
 */
public interface BuiltinTypeHandler extends TypeHandler4 {
    
	void registerReflector(Reflector reflector);
    ReflectClass classReflector();

}
