/* Copyright (C) 2006  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.fileheader;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.slots.*;


/**
 * @exclude
 */
public class FileHeaderVariablePart1 {
    
    // The variable part format is:

    // (int) converter version
    // (byte) freespace system used
    // (int)  freespace address
    // (int) identity ID
    // (long) versionGenerator
	// (int) uuid index ID
	
    
    private static final int LENGTH = 1 + (Const4.INT_LENGTH * 4) + Const4.LONG_LENGTH + Const4.ADDED_LENGTH;
    
    protected final LocalObjectContainer _container;
    
    protected final SystemData _systemData;
    
    private int _id;
    
    public FileHeaderVariablePart1(LocalObjectContainer container, int id, SystemData systemData) {
        _id = id;
        _container = container;
        _systemData = systemData;
    }
    
    public byte getIdentifier() {
        return Const4.HEADER;
    }

    public int ownLength() {
        return LENGTH;
    }

    public void readThis(ByteArrayBuffer buffer) {
		if (Deploy.debug) {
		    buffer.readBegin(getIdentifier());
		}
        _systemData.converterVersion(buffer.readInt());
        _systemData.freespaceSystem(buffer.readByte());
        _systemData.freespaceAddress(buffer.readInt());
        _systemData.identityId(buffer.readInt());
        _systemData.lastTimeStampID(buffer.readLong());
        _systemData.uuidIndexId(buffer.readInt());
    }

    public void writeThis(ByteArrayBuffer buffer) {
		if (Deploy.debug) {
		    buffer.writeBegin(getIdentifier());
		}
        buffer.writeInt(_systemData.converterVersion());
        buffer.writeByte(_systemData.freespaceSystem());
        buffer.writeInt(_systemData.freespaceAddress());
        Db4oDatabase identity = _systemData.identity();
        buffer.writeInt(identity == null ? 0 : identity.getID(systemTransaction()));
        buffer.writeLong(_systemData.lastTimeStampID());
        buffer.writeInt(_systemData.uuidIndexId());
    }

	private Transaction systemTransaction() {
		return _container.systemTransaction();
	}
    
    public void readIdentity(LocalTransaction trans) {
        LocalObjectContainer file = trans.localContainer();
        Db4oDatabase identity = Debug4.staticIdentity ? 
        		Db4oDatabase.STATIC_IDENTITY : 
        		(Db4oDatabase) file.getByID(trans, _systemData.identityId());
        if (null != identity) {
        	// TODO: what?
        	file.activate(trans, identity, new FixedActivationDepth(2));
        	_systemData.identity(identity);
        }
    }

	public Runnable commit() {
		int length = _container.blockConverter().blockAlignedBytes(ownLength());
		if(_id == 0){
			_id = _container.allocatePointerSlot();
		}
		final Slot committedSlot = _container.readPointerSlot(_id);
		final Slot newSlot = allocateSlot(length);
		ByteArrayBuffer buffer = new ByteArrayBuffer(length);
		writeThis(buffer);
		_container.writeEncrypt(buffer, newSlot.address(), 0);
		return new Runnable(){
			public void run() {
				
				// FIXME: This is not transactional !!!
				_container.writePointer(_id, newSlot);
				
				if(committedSlot == null || committedSlot.isNull()){
					return;
				}
				_container.freespaceManager().freeTransactionLogSlot(committedSlot);
			}
		};
	}

	private Slot allocateSlot(int length) {
		Slot reusedSlot = _container.freespaceManager().allocateTransactionLogSlot(length);
		if(reusedSlot != null){
			return reusedSlot;
		}
		return _container.appendBytes(length);
	}

	public int id() {
		return _id;
	}

	public void read() {
    	Slot slot = _container.readPointerSlot(_id);
    	ByteArrayBuffer buffer = _container.readBufferBySlot(slot);
    	readThis(buffer);
	}

}
