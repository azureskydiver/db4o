/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public interface InternalReadContext extends ReadContext{
    
    public ReadWriteBuffer buffer(ReadWriteBuffer buffer);
    
    public ReadWriteBuffer buffer();
    
    public ObjectContainerBase container();

    public int offset();

    public Object read(TypeHandler4 handler);
    
    public ReadWriteBuffer readIndirectedBuffer();

    public void seek(int offset);
    
}
