/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.foundation.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;
import com.db4o.marshall.*;

/**
 * @exclude
 */
public class MarshallingBuffer implements WriteBuffer{
    
    private static final int SIZE_NEEDED = Const4.LONG_LENGTH;
    
    private static final int NO_PARENT = - Integer.MAX_VALUE;
    
    private Buffer _delegate;
    
    private int _lastOffSet;
    
    private int _addressInParent = NO_PARENT;
    
    private List4 _children;
    
    private FieldMetadata _indexedField;
    
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
    
    public void writeBytes(byte[] bytes) {
        prepareWrite(bytes.length);
        _delegate.writeBytes(bytes);
    }

    public void writeInt(int i) {
        prepareWrite();
        _delegate.writeInt(i);
    }
    
    public void writeLong(long l) {
        prepareWrite();
        _delegate.writeLong(l);
    }
    
    private void prepareWrite(){
        prepareWrite(SIZE_NEEDED);
    }
    
    private void prepareWrite(int sizeNeeded){
        if(_delegate == null){
            _delegate = new Buffer(sizeNeeded); 
        }
        _lastOffSet = _delegate.offset();
        if(remainingSize() < sizeNeeded){
            resize(sizeNeeded);
        }
    }

    private int remainingSize() {
        return _delegate.length() - _delegate.offset();
    }

    private void resize(int sizeNeeded) {
        int newSize = _delegate.length() * 2;
        if(newSize - _lastOffSet < sizeNeeded){
            newSize += sizeNeeded;
        }
        Buffer temp = new Buffer(newSize);
        temp.offset(_lastOffSet);
        _delegate.copyTo(temp, 0, 0, _lastOffSet);
        _delegate = temp;
    }
    
    public void transferLastWriteTo(MarshallingBuffer other, boolean storeLengthInLink){
        other.addressInParent(_lastOffSet, storeLengthInLink);
        int length = _delegate.offset() - _lastOffSet;
        other.prepareWrite(length);
        int otherOffset = other._delegate.offset();
        System.arraycopy(_delegate._buffer, _lastOffSet, other._delegate._buffer, otherOffset, length);
        _delegate.offset(_lastOffSet);
        other._delegate.offset(otherOffset + length);
        other._lastOffSet = otherOffset;
    }
    
    private void addressInParent(int offset, boolean storeLengthInLink) {
        _addressInParent = storeLengthInLink ? offset : -offset;
    }

    public void transferContentTo(Buffer buffer){
        System.arraycopy(_delegate._buffer, 0, buffer._buffer, buffer._offset, length());
        buffer._offset += length();
    }
    
    public Buffer testDelegate(){
        return _delegate;
    }
    
    public MarshallingBuffer addChild() {
        return addChild(true, false);
    }
    
    public MarshallingBuffer addChild(boolean reserveLinkSpace, boolean storeLengthInLink) {
        MarshallingBuffer child = new MarshallingBuffer();
        child.addressInParent(offset(), storeLengthInLink);
        _children = new List4(_children, child);
        if(reserveLinkSpace){
            reserveChildLinkSpace(storeLengthInLink);
        }
        return child;
    }

    public void reserveChildLinkSpace(boolean storeLengthInLink) {
        int length = storeLengthInLink ? Const4.INT_LENGTH * 2 : Const4.INT_LENGTH;
        prepareWrite(length);
        _delegate.incrementOffset(length);
    }
    
    public void mergeChildren(MarshallingContext context, int masterAddress, int linkOffset) {
        mergeChildren(context, masterAddress, this, this, linkOffset);
    }

    private static void mergeChildren(MarshallingContext context, int masterAddress, MarshallingBuffer writeBuffer, MarshallingBuffer parentBuffer, int linkOffset) {
        if(parentBuffer._children == null){
            return;
        }
        Iterator4 i = new Iterator4Impl(parentBuffer._children);
        while(i.moveNext()){
            merge(context, masterAddress, writeBuffer, parentBuffer, (MarshallingBuffer) i.current(), linkOffset);
        }
    }
    
    private static void merge(MarshallingContext context, int masterAddress, MarshallingBuffer writeBuffer, MarshallingBuffer parentBuffer, MarshallingBuffer childBuffer, int linkOffset) {
        
        int childLength = childBuffer.length();
        int childPosition = writeBuffer.offset();
        writeBuffer.reserve(childLength);
        
        mergeChildren(context,  masterAddress, writeBuffer, childBuffer, linkOffset);
        
        int savedWriteBufferOffset = writeBuffer.offset();
        writeBuffer.seek(childPosition);
        childBuffer.transferContentTo(writeBuffer._delegate);
        writeBuffer.seek(savedWriteBufferOffset);
        
        parentBuffer.writeLink(context, masterAddress, childBuffer, childPosition + linkOffset, childLength);
    }
    
    public void seek(int offset) {
        _delegate.offset(offset);
    }

    private void reserve(int length) {
        prepareWrite(length);
        _delegate.offset(_delegate.offset() + length );
    }

    private void writeLink(MarshallingContext context, int masterAddress, MarshallingBuffer child, int position, int length){
        int offset = offset();
        _delegate.offset(child.addressInParent());
        _delegate.writeInt(position);
        if(child.storeLengthInLink()){
            _delegate.writeInt(length);
        }
        child.writeIndex(context, masterAddress, position, length);
        _delegate.offset(offset);
        
    }
    
    private void writeIndex(MarshallingContext context, int masterAddress, int position, int length) {
        if(_indexedField != null){
            
            // for now this is a String index only, it takes the entire slot.
            
            StatefulBuffer buffer = new StatefulBuffer(context.transaction(), length);
            buffer.setID(masterAddress + position);
            buffer.address(masterAddress + position);
            transferContentTo(buffer);
            _indexedField.addIndexEntry(context.transaction(), context.objectID(), buffer);
            
        }
    }

    private int addressInParent() {
        if(! hasParent()){
            throw new IllegalStateException();
        }
        if(_addressInParent < 0){
            return - _addressInParent;
        }
        return _addressInParent;
    }

    public void debugDecrementLastOffset(int count){
        _lastOffSet -= count;
    }
    
    public boolean hasParent(){
        return _addressInParent != NO_PARENT;
    }
    
    private boolean storeLengthInLink(){
        return _addressInParent > 0;
    }

    public void requestIndexEntry(FieldMetadata fieldMetadata) {
        _indexedField = fieldMetadata;
    }

    public int marshalledLength(MarshallingContext context) {
        int length = context.requiredLength(this, doBlockAlign());
        if(doBlockAlign()){
            blockAlign(length);
        }
        if(_children != null){
            Iterator4 i = new Iterator4Impl(_children);
            while(i.moveNext()){
                length += ((MarshallingBuffer) i.current()).marshalledLength(context);
            }
        }
        return length;
    }

    private void blockAlign(int length) {
        if(length > _delegate.length()){
            int sizeNeeded = length - _delegate.length();
            prepareWrite(sizeNeeded);
        }
        _delegate.offset(length);
    }

    private boolean doBlockAlign() {
        return ! hasParent() || _indexedField != null;
    }

}
