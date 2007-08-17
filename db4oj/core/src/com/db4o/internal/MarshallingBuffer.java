/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.marshall.*;

/**
 * @exclude
 */
public class MarshallingBuffer implements WriteBuffer{
    
    private static final int SIZE_NEEDED = Const4.LONG_LENGTH; 
    
    private Buffer _delegate;
    
    private int _lastOffSet;

    public int length() {
        if(_delegate == null){
            return 0;
        }
        return _delegate.offset();
    }
    
    public void writeByte(byte b) {
        prepareWrite();
        _delegate.writeByte(b);
    }

    public void writeInt(int i) {
        prepareWrite();
        _delegate.writeInt(i);
    }
    
    private void prepareWrite(){
        prepareWrite(SIZE_NEEDED);
    }
    
    private void prepareWrite(int sizeNeeded){
        if(_delegate == null){
            _delegate = new Buffer(sizeNeeded); 
        }
        
        _lastOffSet = _delegate.offset();
        
        if(_delegate.length() - _lastOffSet < sizeNeeded){
            int newLength = _delegate.length() * 2;
            if(newLength - _lastOffSet < sizeNeeded){
                newLength += sizeNeeded;
            }
            Buffer temp = new Buffer(newLength);
            temp.offset(_lastOffSet);
            _delegate.copyTo(temp, 0, 0, _lastOffSet);
            _delegate = temp;
        }
    }
    
    public void transferLastWriteTo(MarshallingBuffer other){
        int length = _delegate.offset() - _lastOffSet;
        other.prepareWrite(length);
        int otherOffset = other._delegate.offset();
        System.arraycopy(_delegate._buffer, _lastOffSet, other._delegate._buffer, otherOffset, length);
        _delegate.offset(_lastOffSet);
        _lastOffSet -= length;
        other._delegate.offset(otherOffset + length);
        other._lastOffSet = otherOffset;
    }
    
    public void transferContentTo(Buffer buffer){
        System.arraycopy(_delegate._buffer, 0, buffer._buffer, buffer._offset, length());
        buffer._offset += length();
    }
    
    public Buffer testDelegate(){
        return _delegate;
    }

}
