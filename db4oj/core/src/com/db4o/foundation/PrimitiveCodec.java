/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;


public final class PrimitiveCodec {
	
	public static final int INT_LENGTH = 4;
	
	public static final int LONG_LENGTH = 8;
	
	
	public static final int readInt(byte[] buffer, int offset){
        offset += 3;
        return (buffer[offset] & 255) | (buffer[--offset] & 255)
            << 8 | (buffer[--offset] & 255)
            << 16 | buffer[--offset]
            << 24;
	}
	
	public static final void writeInt(byte[] buffer, int offset, int val){
        offset += 3;
        buffer[offset] = (byte)val;
        buffer[--offset] = (byte) (val >>= 8);
        buffer[--offset] = (byte) (val >>= 8);
        buffer[--offset] = (byte) (val >> 8);
	}
	
	public static final void writeLong(byte[] buffer, long val){
		writeLong(buffer, 0, val);
	}
	
	public static final void writeLong(byte[] buffer, int offset, long val){
		for (int i = 0; i < LONG_LENGTH; i++){
			buffer[offset++] = (byte) (val >> ((7 - i) * 8));
		}
	}
	
	public static final long readLong(byte[] buffer, int offset){
		long ret = 0;
		for (int i = 0; i < LONG_LENGTH; i++){
			ret = (ret << 8) + (buffer[offset++] & 0xff);
		}
		return ret;
	}

}
