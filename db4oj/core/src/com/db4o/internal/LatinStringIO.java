/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.marshall.*;



/**
 * @exclude
 */
public class LatinStringIO {
    
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
	
	public int length(String str){
		return str.length() + Const4.OBJECT_LENGTH + Const4.INT_LENGTH;
	}
	
	public String read(ReadBuffer buffer, int length){
	    char[] chars = new char[length];
		for(int ii = 0; ii < length; ii++){
			chars[ii] = (char)(buffer.readByte() & 0xff);
		}
		return new String(chars,0,length);
	}
	
	public String read(byte[] bytes){
	    char[] chars = new char[bytes.length];
	    for(int i = 0; i < bytes.length; i++){
	        chars[i] = (char)(bytes[i]& 0xff);
	    }
	    return new String(chars,0,bytes.length);
	}
	
	public int shortLength(String str){
		return str.length() + Const4.INT_LENGTH;
	}
	
	public void write(WriteBuffer buffer, String str){
	    final int length = str.length();
	    char[] chars = new char[length];
	    str.getChars(0, length, chars, 0);
	    for (int i = 0; i < length; i ++){
			buffer.writeByte((byte) (chars[i] & 0xff));
		}
	}
	
	byte[] write(String str){
	    final int length = str.length();
        char[] chars = new char[length];
        str.getChars(0, length, chars, 0);
	    byte[] bytes = new byte[length];
	    for (int i = 0; i < length; i ++){
	        bytes[i] = (byte) (chars[i] & 0xff);
	    }
	    return bytes;
	}
	
}
