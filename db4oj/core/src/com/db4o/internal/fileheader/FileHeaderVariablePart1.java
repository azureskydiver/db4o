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
public class FileHeaderVariablePart1 extends PersistentBase{
    
    // The variable part format is:

    // (int) converter version
    // (byte) freespace system used
    // (int)  freespace address
    // (int) identity ID
    // (long) versionGenerator
	// (int) uuid index ID
	// (byte) idSystem
	// (int) idSystem ID
	
    
    private static final int LENGTH = 2 + (Const4.INT_LENGTH * 5) + Const4.LONG_LENGTH + Const4.ADDED_LENGTH;
    
    private final LocalObjectContainer _container;
    
    private final SystemData _systemData;
    
    private int _identityId;
    
    public FileHeaderVariablePart1(LocalObjectContainer container, int id, SystemData systemData) {
        setID(id);
        _container = container;
        _systemData = systemData;
    }
    
    public byte getIdentifier() {
        return Const4.HEADER;
    }

    public int ownLength() {
        return LENGTH;
    }

    public void readThis(Transaction trans, ByteArrayBuffer reader) {
        _systemData.converterVersion(reader.readInt());
        _systemData.freespaceSystem(reader.readByte());
        _systemData.freespaceAddress(reader.readInt());
        _identityId = reader.readInt();
        _systemData.lastTimeStampID(reader.readLong());
        _systemData.uuidIndexId(reader.readInt());
        
        if(reader.eof()){
        	// older versions of the file header don't have IdSystem information.
        	return;
        }
        _systemData.idSystemType(reader.readByte());
        _systemData.idSystemID(reader.readInt());
    }

    public void writeThis(Transaction trans, ByteArrayBuffer writer) {
        writer.writeInt(_systemData.converterVersion());
        writer.writeByte(_systemData.freespaceSystem());
        writer.writeInt(_systemData.freespaceAddress());
        writer.writeInt(_systemData.identity().getID(trans));
        writer.writeLong(_systemData.lastTimeStampID());
        writer.writeInt(_systemData.uuidIndexId());
        writer.writeByte(_systemData.idSystemType());
        writer.writeInt(_systemData.idSystemID());
    }
    
    public void readIdentity(LocalTransaction trans) {
        LocalObjectContainer file = trans.localContainer();
        Db4oDatabase identity = Debug4.staticIdentity ? Db4oDatabase.STATIC_IDENTITY : (Db4oDatabase) file.getByID(trans, _identityId);
        if (null != identity) {
        	// TODO: what?
        	file.activate(trans, identity, new FixedActivationDepth(2));
        	_systemData.identity(identity);
        }
       
    }
    
    @Override
    protected ByteArrayBuffer readBufferById(Transaction trans) {
    	Slot slot = _container.readPointerSlot(_id);
    	return _container.readBufferBySlot(slot);
    }

}
