/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.fileheader;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.internal.*;


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
    
    private static final int LENGTH = 1 + (Const4.INT_LENGTH * 4) + Const4.LONG_LENGTH + Const4.ADDED_LENGTH; 
    
    private final SystemData _systemData;
    
    public FileHeaderVariablePart1(int id, SystemData systemData) {
        setID(id);
        _systemData = systemData;
    }
    
    SystemData systemData() {
    	return _systemData;
    }

    public byte getIdentifier() {
        return Const4.HEADER;
    }

    public int ownLength() {
        return LENGTH;
    }

    public void readThis(Transaction trans, Buffer reader) {
        _systemData.converterVersion(reader.readInt());
        _systemData.freespaceSystem(reader.readByte());
        _systemData.freespaceAddress(reader.readInt());
        readIdentity(trans, reader.readInt());
        _systemData.lastTimeStampID(reader.readLong());
        _systemData.uuidIndexId(reader.readInt());
    }

    public void writeThis(Transaction trans, Buffer writer) {
        writer.writeInt(_systemData.converterVersion());
        writer.append(_systemData.freespaceSystem());
        writer.writeInt(_systemData.freespaceAddress());
        writer.writeInt(_systemData.identity().getID(trans));
        writer.writeLong(_systemData.lastTimeStampID());
        writer.writeInt(_systemData.uuidIndexId());
    }
    
    private void readIdentity(Transaction trans, int identityID) {
        LocalObjectContainer file = trans.i_file;
        Db4oDatabase identity = (Db4oDatabase) file.getByID1(trans, identityID);
        file.activate1(trans, identity, 2);
        _systemData.identity(identity);
    }

}
