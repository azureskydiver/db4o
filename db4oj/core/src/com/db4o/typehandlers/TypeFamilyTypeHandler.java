/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.typehandlers;

import com.db4o.reflect.ReflectClass;

/**
 * @exclude
 */
public interface TypeFamilyTypeHandler extends TypeHandler4{
	
    public boolean canHold(ReflectClass claxx);

    public boolean isSimple();

    public int linkLength();

}
