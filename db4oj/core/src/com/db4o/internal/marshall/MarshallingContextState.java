/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;


/**
 * @exclude
 */
public class MarshallingContextState {
    
    final MarshallingBuffer _buffer;

    public MarshallingContextState(MarshallingBuffer buffer) {
        _buffer = buffer;
    }

}
