/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

import com.db4o.internal.*;


/**
 * this interface is passed to {@link TypeHandler4}
 * during marshalling and provides methods to marshall
 * objects. 
 */
public interface WriteContext {

    WriteBuffer newBuffer(int length);


}
