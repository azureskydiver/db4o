/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside;

import java.io.*;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.foundation.network.*;
import com.db4o.inside.*;

/**
 * public for .NET conversion reasons
 * 
 * TODO: Split this class for individual usecases. Only use the member
 * variables needed for the respective usecase.
 * 
 * @exclude
 */
public final class StatefulBuffer extends Buffer {
    

    private int i_address;
    private int _addressOffset;

    private int i_cascadeDelete; 

    private Tree i_embedded;
    private int i_id;

    // carries instantiation depth through the reading process
    private int i_instantionDepth;
    private int i_length;

    Transaction i_trans;

    // carries updatedepth depth through the update process
    // and carries instantiation information through the reading process 
    private int i_updateDepth = 1;
    
    public int _payloadOffset;
    

    public StatefulBuffer(Transaction a_trans, int a_initialBufferSize) {
        i_trans = a_trans;
        i_length = a_initialBufferSize;
        _buffer = new byte[i_length];
    }

    public StatefulBuffer(Transaction a_trans, int a_address, int a_initialBufferSize) {
        this(a_trans, a_initialBufferSize);
        i_address = a_address;
    }

    public StatefulBuffer(StatefulBuffer parent, StatefulBuffer[] previousRead, int previousCount) {
        previousRead[previousCount++] = this;
        int parentID = parent.readInt();
        i_length = parent.readInt();
        i_id = parent.readInt();
        previousRead[parentID].addEmbedded(this);
        i_address = parent.readInt();
        i_trans = parent.getTransaction();
        _buffer = new byte[i_length];
        System.arraycopy(parent._buffer, parent._offset, _buffer, 0, i_length);
        parent._offset += i_length;
        if (previousCount < previousRead.length) {
            new StatefulBuffer(parent, previousRead, previousCount);
        }
    }

    //	void debug(){
    //		if(Debug.current){
    //			System.out.println("Address: " + i_address + " ID:" + i_id);
    //			if(i_embedded != null){
    //				System.out.println("Children:");
    //				Iterator i = i_embedded.iterator();
    //				while(i.hasNext()){
    //					((YapBytes)(i.next())).debug();
    //				}
    //			}
    //		}
    //	}

    public void addEmbedded(StatefulBuffer a_bytes) {
        i_embedded = Tree.add(i_embedded, new TreeIntObject(a_bytes.getID(), a_bytes));
    }


    public int appendTo(final Buffer a_bytes, int a_id) {
        a_id++;
        a_bytes.writeInt(i_length);
        a_bytes.writeInt(i_id);
        a_bytes.writeInt(i_address);
        a_bytes.append(_buffer);
        final int[] newID = { a_id };
        final int myID = a_id;
        forEachEmbedded(new VisitorYapBytes() {
            public void visit(StatefulBuffer a_embedded) {
                a_bytes.writeInt(myID);
                newID[0] = a_embedded.appendTo(a_bytes, newID[0]);
            }
        });
        return newID[0];
    }

    public int cascadeDeletes() {
        return i_cascadeDelete;
    }

    public void debugCheckBytes() {
        if (Debug.xbytes) {
            if (_offset != i_length) {
                // Db4o.log("!!! YapBytes.debugCheckBytes not all bytes used");
                // This is normal for writing The FreeSlotArray, becauce one
                // slot is possibly reserved by it's own pointer.
            }
        }
    }

    public int embeddedCount() {
        final int[] count = { 0 };
        forEachEmbedded(new VisitorYapBytes() {
            public void visit(StatefulBuffer a_bytes) {
                count[0] += 1 + a_bytes.embeddedCount();
            }
        });
        return count[0];
    }

    public int embeddedLength() {
        final int[] length = { 0 };
        forEachEmbedded(new VisitorYapBytes() {
            public void visit(StatefulBuffer a_bytes) {
                length[0] += a_bytes.getLength() + a_bytes.embeddedLength();
            }
        });
        return length[0];
    }

    void forEachEmbedded(final VisitorYapBytes a_visitor) {
        if (i_embedded != null) {
            i_embedded.traverse(new Visitor4() {
                public void visit(Object a_object) {
                    a_visitor.visit((StatefulBuffer) ((TreeIntObject)a_object)._object);
                }
            });
        }
    }

    public int getAddress() {
        return i_address;
    }
    
    public int addressOffset(){
        return _addressOffset;
    }

    public int getID() {
        return i_id;
    }

    public int getInstantiationDepth() {
        return i_instantionDepth;
    }

    public int getLength() {
        return i_length;
    }

    public YapStream getStream() {
        return i_trans.stream();
    }
    
    public YapStream stream(){
        return i_trans.stream();
    }
    
    public YapFile file(){
        return i_trans.i_file;
    }

    public Transaction getTransaction() {
        return i_trans;
    }

    public int getUpdateDepth() {
        return i_updateDepth;
    }
    
    public byte[] getWrittenBytes(){
        byte[] bytes = new byte[_offset];
        System.arraycopy(_buffer, 0, bytes, 0, _offset);
        return bytes;
    }
    
    public int preparePayloadRead() {
        int newPayLoadOffset = readInt();
        int length = readInt();
        int linkOffSet = _offset;
        _offset = newPayLoadOffset;
        _payloadOffset += length;
        return linkOffSet;
    }

    public void read() {
        stream().readBytes(_buffer, i_address,_addressOffset, i_length);
    }

    public final boolean read(YapSocket sock) throws IOException {
        int offset = 0;
        int length = i_length;
        while (length > 0) {
            int read = sock.read(_buffer, offset, length);
			if(read<0) {
				return false;
			}
            offset += read;
            length -= read;
        }
		return true;
    }

    public final StatefulBuffer readEmbeddedObject() {
        int id = readInt();
        int length = readInt();
        StatefulBuffer bytes = null;
        Tree tio = TreeInt.find(i_embedded, id);
        if (tio != null) {
            bytes = (StatefulBuffer) ((TreeIntObject)tio)._object; 
        }else{
            bytes = stream().readWriterByAddress(i_trans, id, length);
            if (bytes != null) {
                bytes.setID(id);
            }
        }
        if(bytes != null){
            bytes.setUpdateDepth(getUpdateDepth());
            bytes.setInstantiationDepth(getInstantiationDepth());
        }
        return bytes;
    }

    public final StatefulBuffer readYapBytes() {
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

    public void setCascadeDeletes(int depth) {
        i_cascadeDelete = depth;
    }

    public void setID(int a_id) {
        i_id = a_id;
    }

    public void setInstantiationDepth(int a_depth) {
        i_instantionDepth = a_depth;
    }

    public void setTransaction(Transaction aTrans) {
        i_trans = aTrans;
    }

    public void setUpdateDepth(int a_depth) {
        i_updateDepth = a_depth;
    }

    public void slotDelete() {
        i_trans.slotDelete(i_id, i_address, i_length);
    }
    
    public void trim4(int a_offset, int a_length) {
        byte[] temp = new byte[a_length];
        System.arraycopy(_buffer, a_offset, temp, 0, a_length);
        _buffer = temp;
        i_length = a_length;
    }

    public void useSlot(int a_adress) {
        i_address = a_adress;
        _offset = 0;
    }

    public void useSlot(int a_adress, int a_length) {
        i_address = a_adress;
        _offset = 0;
        if (a_length > _buffer.length) {
            _buffer = new byte[a_length];
        }
        i_length = a_length;
    }

    public void useSlot(int a_id, int a_adress, int a_length) {
        i_id = a_id;
        useSlot(a_adress, a_length);
    }

    public void write() {
        if (Debug.xbytes) {
            debugCheckBytes();
        }
        file().writeBytes(this, i_address, _addressOffset);
    }

    public void writeEmbedded() {
        final StatefulBuffer finalThis = this;
        forEachEmbedded(new VisitorYapBytes() {
            public void visit(StatefulBuffer a_bytes) {
                a_bytes.writeEmbedded();
                stream().writeEmbedded(finalThis, a_bytes);
            }
        });
        
        // TODO: It may be possible to remove the following to 
        // allow indexes to be created from the bytes passed
        // from the client without having to reread. Currently
        // the bytes don't seem to be found and there is a
        // problem with encryption during the write process.

        i_embedded = null; // no reuse !!!
    }

    public void writeEmbeddedNull() {
        writeInt(0);
        writeInt(0);
    }

    public void writeEncrypt() {
        if (Deploy.debug) {
            debugCheckBytes();
        }
        writeEncrypt(file(),i_address, _addressOffset);
    }
    
    /* Only used for Strings, topLevel therefore means aligning blocksize, so
     * index will be possible.
     */
    public void writePayload(StatefulBuffer payLoad, boolean topLevel){
        checkMinimumPayLoadOffsetAndWritePointerAndLength(payLoad.getLength(), topLevel);
        System.arraycopy(payLoad._buffer, 0, _buffer, _payloadOffset, payLoad._buffer.length);
        transferPayLoadAddress(payLoad, _payloadOffset);
        _payloadOffset += payLoad._buffer.length;
    }
    
    private void checkMinimumPayLoadOffsetAndWritePointerAndLength(int length, boolean alignToBlockSize){
        if(_payloadOffset <= _offset + (YapConst.INT_LENGTH * 2)){
            _payloadOffset = _offset + (YapConst.INT_LENGTH * 2);
        }
        if(alignToBlockSize){
            _payloadOffset = stream().alignToBlockSize(_payloadOffset);
        }
        writeInt(_payloadOffset);
        
        // TODO: This length is here for historical reasons. 
        //       It's actually never really needed during reading.
        //       It's only necessary because array and string used
        //       to consist of a double pointer in marshaller family 0
        //       and it was not considered a good idea to change
        //       their linkLength() values for compatibility reasons
        //       with marshaller family 0.
        writeInt(length);
    }
    
    public int reserveAndPointToPayLoadSlot(int length){
        checkMinimumPayLoadOffsetAndWritePointerAndLength(length, false);
        int linkOffset = _offset;
        _offset = _payloadOffset;
        _payloadOffset += length;
        return linkOffset;
    }
    
    public Buffer readPayloadWriter(int offset, int length){
        StatefulBuffer payLoad = new StatefulBuffer(i_trans, 0, length);
        System.arraycopy(_buffer,offset, payLoad._buffer, 0, length);
        transferPayLoadAddress(payLoad, offset);
        return payLoad;
    }

    private void transferPayLoadAddress(StatefulBuffer toWriter, int offset) {
        int blockedOffset = offset / stream().blockSize();
        toWriter.i_address = i_address + blockedOffset;
        toWriter.i_id = toWriter.i_address;
        toWriter._addressOffset = _addressOffset;
    }

    void writeShortString(String a_string) {
        writeShortString(i_trans, a_string);
    }

    public void moveForward(int length) {
        _addressOffset += length;
    }
    
    public void writeForward() {
        write();
        _addressOffset += i_length;
        _offset = 0;
    }
    
    public String toString(){
        if(! Debug4.prettyToStrings){
            return super.toString();
        }
        return "id " + i_id + " adr " + i_address + " len " + i_length;
    }
    
    public void noXByteCheck() {
        if(Debug.xbytes && Deploy.overwrite){
            setID(YapConst.IGNORE_ID);
        }
    }
    
	public void writeIDs(IntIterator4 idIterator, int maxCount ) {
		int savedOffset = _offset; 
        writeInt(0);
        int actualCount = 0;
        while(idIterator.moveNext()){
            writeInt(idIterator.currentInt());
            actualCount ++;
            if(actualCount >= maxCount){
            	break;
            }
        }
        int secondSavedOffset = _offset;
        _offset = savedOffset;
        writeInt(actualCount);
        _offset = secondSavedOffset;
	}
    

}
