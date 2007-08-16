/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;


/**
 * a buffer interface with methods to write.
 */
public interface WriteBuffer {

    void writeByte(byte b);

    void writeInt(int i);
    
}
