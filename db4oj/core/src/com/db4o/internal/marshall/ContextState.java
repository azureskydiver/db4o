/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;


/**
 * @exclude
 */
public class ContextState {
    
    public final int _offset;
    
    public final int _currentSlot;
    
    public ContextState(int offset, int currentSlot){
        _offset = offset;
        _currentSlot = currentSlot;
    }

}
