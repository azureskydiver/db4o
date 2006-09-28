/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.header;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.inside.*;


/**
 * @exclude
 */
public class FileHeaderVariablePart1 extends YapMeta{
    
    // The variable part format is:

    // (byte) freespace system used
    // (int)  freespace address
    // (int) converter version
    // (int) identity ID
    // (long) versionGenerator
    
    private static final int LENGTH = 1 + (YapConst.INT_LENGTH * 3) + YapConst.LONG_LENGTH; 
    
    private final SystemData _systemData;
    
    public FileHeaderVariablePart1(int id, SystemData systemData) {
        setID(id);
        _systemData = systemData;
    }

    public byte getIdentifier() {
        return YapConst.HEADER;
    }

    public int ownLength() {
        return LENGTH;
    }

    public void readThis(Transaction trans, YapReader reader) {
        _systemData.freespaceSystem(reader.readByte());
        _systemData.freespaceAddress(reader.readInt());
        _systemData.converterVersion(reader.readInt());
        readIdentity(trans, reader.readInt());
        _systemData.lastTimeStampID(reader.readLong());
    }

    public void writeThis(Transaction trans, YapReader writer) {
        writer.append(_systemData.freespaceSystem());
        writer.writeInt(_systemData.freespaceAddress());
        writer.writeInt(_systemData.converterVersion());
        writer.writeInt(_systemData.identity().getID(trans));
        writer.writeLong(_systemData.lastTimeStampID());
    }
    
    private void readIdentity(Transaction trans, int identityID) {
        YapFile file = trans.i_file;
        Db4oDatabase identity = (Db4oDatabase) file.getByID1(trans, identityID);
        file.activate1(trans, identity, 2);
        _systemData.identity(identity);
    }

}
