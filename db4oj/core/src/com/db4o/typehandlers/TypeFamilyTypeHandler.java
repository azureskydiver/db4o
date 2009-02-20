/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.typehandlers;


/**
 * @exclude
 */
public interface TypeFamilyTypeHandler extends TypeHandler4{
	
    public boolean isSimple();

    public int linkLength();

}
