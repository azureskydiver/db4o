/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.internal.slots.*;

/**
 * public for .NET conversion reasons
 * 
 * TODO: Split this class for individual usecases. Only use the member
 * variables needed for the respective usecase.
 * 
 * @exclude
 */
public final class StatefulBuffer extends ByteArrayBuffer {
	
    private int i_address;
    private int _addressOffset;

    private int i_cascadeDelete; 

    private int i_id;

    private int i_length;

    Transaction i_trans;

    public int _payloadOffset;
    

    public StatefulBuffer(Transaction a_trans, int a_initialBufferSize) {
        i_trans = a_trans;
        i_length = a_initialBufferSize;
        _buffer = new byte[i_length];
    }
    
    public StatefulBuffer(Transaction a_trans, int address, int length) {
        this(a_trans, length);
        i_address = address;
    }
    
    public StatefulBuffer(Transaction trans, Slot slot){
        this(trans, slot.address(), slot.length());
    }

    public StatefulBuffer(Transaction trans, Pointer4 pointer){
        this(trans, pointer._slot);
        i_id = pointer._id;
    }


    public void debugCheckBytes() {
        if (Debug4.xbytes) {
            if (_offset != i_length) {
                // Db4o.log("!!! YapBytes.debugCheckBytes not all bytes used");
                // This is normal for writing The FreeSlotArray, becauce one
                // slot is possibly reserved by it's own pointer.
            }
        }
    }

    public int getAddress() {
        return i_address;
    }
    
    public int getID() {
        return i_id;
    }

    public int length() {
        return i_length;
    }

    public ObjectContainerBase container(){
        return i_trans.container();
    }
    
    public LocalObjectContainer file(){
        return ((LocalTransaction)i_trans).file();
    }

    public Transaction transaction() {
        return i_trans;
    }

    public byte[] getWrittenBytes(){
        byte[] bytes = new byte[_offset];
        System.arraycopy(_buffer, 0, bytes, 0, _offset);
        return bytes;
    }
    
    public void read() throws Db4oIOException {
        container().readBytes(_buffer, i_address,_addressOffset, i_length);
    }

    public final StatefulBuffer readStatefulBuffer() {
        int length = readInt();
        if (length == 0) {
            return null;
        }
        StatefulBuffer yb = new StatefulBuffer(i_trans, length);
        System.arraycopy(_buffer, _offset, yb._buffer, 0, length);
        _offset += length;
        return yb;
    }

    public void removeFirstBytes(int aLength) {
        i_length -= aLength;
        byte[] temp = new byte[i_length];
        System.arraycopy(_buffer, aLength, temp, 0, i_length);
        _buffer = temp;
        _offset -= aLength;
        if (_offset < 0) {
            _offset = 0;
        }
    }

    public void address(int a_address) {
        i_address = a_address;
    }

    public void setID(int a_id) {
        i_id = a_id;
    }

    public void setTransaction(Transaction aTrans) {
        i_trans = aTrans;
    }

    public void slotDelete() {
        i_trans.slotDelete(i_id, slot());
    }
    
    public void useSlot(int a_adress) {
        i_address = a_adress;
        _offset = 0;
    }

    // FIXME: FB remove
    public void useSlot(int address, int length) {
    	useSlot(new Slot(address, length));
    }
    
    public void useSlot(Slot slot) {
        i_address = slot.address();
        _offset = 0;
        if (slot.length() > _buffer.length) {
            _buffer = new byte[slot.length()];
        }
        i_length = slot.length();
    }

    // FIXME: FB remove
    public void useSlot(int a_id, int a_adress, int a_length) {
        i_id = a_id;
        useSlot(a_adress, a_length);
    }
    
    public void write() {
        if (Debug4.xbytes) {
            debugCheckBytes();
        }
        file().writeBytes(this, i_address, _addressOffset);
    }

    public void writeEncrypt() {
        if (Deploy.debug) {
            debugCheckBytes();
        }
        file().writeEncrypt(this, i_address, _addressOffset);
    }
        
    public ByteArrayBuffer readPayloadWriter(int offset, int length){
        StatefulBuffer payLoad = new StatefulBuffer(i_trans, 0, length);
        System.arraycopy(_buffer,offset, payLoad._buffer, 0, length);
        transferPayLoadAddress(payLoad, offset);
        return payLoad;
    }

    private void transferPayLoadAddress(StatefulBuffer toWriter, int offset) {
        int blockedOffset = offset / container().blockSize();
        toWriter.i_address = i_address + blockedOffset;
        toWriter.i_id = toWriter.i_address;
        toWriter._addressOffset = _addressOffset;
    }

    public void moveForward(int length) {
        _addressOffset += length;
    }
    
    public String toString(){
        return "id " + i_id + " adr " + i_address + " len " + i_length;
    }
    
    public void noXByteCheck() {
        if(Debug4.xbytes && Deploy.overwrite){
            setID(Const4.IGNORE_ID);
        }
    }
	
	public Slot slot(){
		return new Slot(i_address, i_length);
	}
	
	public Pointer4 pointer(){
	    return new Pointer4(i_id, slot());
	}
	
    public int cascadeDeletes() {
        return i_cascadeDelete;
    }
    
    public void setCascadeDeletes(int depth) {
        i_cascadeDelete = depth;
    }

}
