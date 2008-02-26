/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.reflect.*;


/**
 * @exclude
 */
public interface BuiltinTypeHandler extends TypeHandler4 {
    
    ReflectClass classReflector(Reflector reflector);

}
