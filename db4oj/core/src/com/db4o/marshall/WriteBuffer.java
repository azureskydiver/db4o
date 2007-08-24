/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;


/**
 * a buffer interface with write methods.
 */
public interface WriteBuffer {

    /**
     * writes a single byte to the buffer.
     * @param b the byte
     */
    void writeByte(byte b);

    /**
     * writes an int to the buffer.
     * @param i the int
     */
    void writeInt(int i);
    
}
