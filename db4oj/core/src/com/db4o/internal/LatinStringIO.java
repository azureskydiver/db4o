/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.marshall.*;



/**
 * @exclude
 */
public class LatinStringIO {
    
    protected char[] chars = new char[0];
    
    public int bytesPerChar(){
        return 1;
    }
    
    public byte encodingByte(){
		return Const4.ISO8859;
	}
    
    static LatinStringIO forEncoding(byte encodingByte){
        switch (encodingByte) {
        case Const4.ISO8859:
        	return new LatinStringIO();
        default:
            return new UnicodeStringIO();
        }
    }
	
	public int length(String a_string){
		return a_string.length() + Const4.OBJECT_LENGTH + Const4.INT_LENGTH;
	}
	
	protected void checkBufferLength(int a_length){
	    if(a_length > chars.length){
	        chars = new char[a_length];
	    }
	}
	
	public String read(Buffer bytes, int a_length){
	    checkBufferLength(a_length);
		for(int ii = 0; ii < a_length; ii++){
			chars[ii] = (char)(bytes._buffer[bytes._offset ++]& 0xff);
		}
		return new String(chars,0,a_length);
	}
	
	public String read(byte[] a_bytes){
	    checkBufferLength(a_bytes.length);
	    for(int i = 0; i < a_bytes.length; i++){
	        chars[i] = (char)(a_bytes[i]& 0xff);
	    }
	    return new String(chars,0,a_bytes.length);
	}
	
	public int shortLength(String a_string){
		return a_string.length() + Const4.INT_LENGTH;
	}
	
	protected int marshalledLength(String str){
	    final int len = str.length();
	    checkBufferLength(len);
	    str.getChars(0, len, chars, 0);
	    return len;
	}
	
	public void write(WriteBuffer buffer, String string){
	    final int len = marshalledLength(string);
	    for (int i = 0; i < len; i ++){
			buffer.writeByte((byte) (chars[i] & 0xff));
		}
	}
	
	byte[] write(String string){
	    final int len = marshalledLength(string);
	    byte[] bytes = new byte[len];
	    for (int i = 0; i < len; i ++){
	        bytes[i] = (byte) (chars[i] & 0xff);
	    }
	    return bytes;
	}
	
}
