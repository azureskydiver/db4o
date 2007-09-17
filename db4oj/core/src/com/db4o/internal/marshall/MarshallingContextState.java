/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;


/**
 * @exclude
 */
public class MarshallingContextState {
    
    public final MarshallingBuffer _buffer;
    
    public final int _fieldWriteCount;

    public MarshallingContextState(MarshallingBuffer buffer, int fieldWriteCount) {
        _buffer = buffer;
        _fieldWriteCount = fieldWriteCount;
    }

}
