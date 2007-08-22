/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.foundation.*;
import com.db4o.marshall.*;

/**
 * @exclude
 */
public class MarshallingBuffer implements WriteBuffer{
    
    private static final int SIZE_NEEDED = Const4.LONG_LENGTH;
    
    private static final int LINK_LENGTH = Const4.INT_LENGTH + Const4.ID_LENGTH;
    
    private Buffer _delegate;
    
    private int _lastOffSet;
    
    private int _addressInParent;
    
    private Collection4 _children;    

    public int length() {
        return offset();
    }
    
    public int offset(){
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
    
    public int transferContentTo(Buffer buffer){
        int offset = buffer._offset;
        System.arraycopy(_delegate._buffer, 0, buffer._buffer, buffer._offset, length());
        buffer._offset += length();
        return offset;
    }
    
    public Buffer testDelegate(){
        return _delegate;
    }

    public MarshallingBuffer addChild() {
        MarshallingBuffer child = new MarshallingBuffer();
        if(_children == null){
            _children = new Collection4();
        }
        child._addressInParent = offset();
        _children.add(child);
        _delegate.incrementOffset(LINK_LENGTH);
        return child;
    }

    public int mergeChildren(int linkOffset) {
        if(_children == null){
            return 0;
        }
        int linkOffSetAddress = linkOffset + offset();
        Iterator4 i = _children.iterator();
        while(i.moveNext()){
            MarshallingBuffer child = (MarshallingBuffer) i.current();
            int childLengthBeforeMerge = child.length();
            linkOffSetAddress = child.mergeChildren(linkOffSetAddress);
            prepareWrite(child.length());
            int offset = child.transferContentTo(_delegate);
            writeLink(child, offset, childLengthBeforeMerge);
        }
        return linkOffSetAddress;
    }
    
    private void writeLink(MarshallingBuffer child, int offset, int length){
        int savedOffset = _delegate.offset();
        _delegate.offset(child._addressInParent);
        _delegate.writeInt(offset);
        _delegate.writeInt(length);
        _delegate.offset(savedOffset);
    }

}
