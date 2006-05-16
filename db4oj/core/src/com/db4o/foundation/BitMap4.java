/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;


/**
 * @exclude
 */
public class BitMap4 {
    
    private final byte[] _bits;
    
    public BitMap4(int numBits){
        int byteCount = (numBits + 7) / 8 ;
        _bits = new byte[byteCount];
    }
    
    /** "readFrom  buffer" constructor **/
    public BitMap4(byte[] buffer, int pos, int numBits){
        this(numBits);
        System.arraycopy(buffer, pos, _bits, 0, _bits.length);
    }
    
    public boolean isTrue(int bit) {
        int offSetInArray = bit / 8;
        int offSetInByte = bit % 8;
        return ((_bits[offSetInArray]>>>offSetInByte)&1)!=0;
    }
    
    public int marshalledLength(){
        return _bits.length;
    }
    
    public void setFalse(int bit){
        int offSetInArray = bit / 8;
        int offSetInByte = bit % 8;
        byte mask=(byte)(1 << offSetInByte);
        _bits[offSetInArray]&= ~mask;
    }
    
    public void setTrue(int bit){
        int offSetInArray = bit / 8;
        int offSetInByte = bit % 8;
        byte mask=(byte)(1 << offSetInByte);
        _bits[offSetInArray]|=mask;
    }
    
    public void writeTo(byte[] bytes, int pos){
        System.arraycopy(_bits, 0, bytes, pos, _bits.length);
    }

}
