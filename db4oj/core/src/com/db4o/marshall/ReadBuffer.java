/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

/**
 * a buffer interface with methods to read.
 */
public interface ReadBuffer {
    
    /**
     * reads a byte from the buffer.
     * @return the byte
     */
    byte readByte();
    
    /**
     * reads an array of bytes from the buffer.
     * The length of the array that is passed as a parameter specifies the
     * number of bytes that are to be read. The passed bytes buffer parameter
     * is directly filled.  
     * @param bytes the byte array to read the bytes into.
     */
    void readBytes(byte[] bytes);

    /**
     * reads an int from the buffer.
     * @return the int
     */
    int readInt();
    
    /**
     * reads a long from the buffer.
     * @return the long
     */
    long readLong();

}
