/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.fileheader;

import com.db4o.internal.*;

/**
 * @exclude
 */
public class FileHeaderVariablePart2 extends FileHeaderVariablePart1 {
	
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


	public FileHeaderVariablePart2(LocalObjectContainer container, int id,
			SystemData systemData) {
		super(container, id, systemData);
	}
	
	@Override
	public int ownLength() {
		return LENGTH;
	}
	
	@Override
	public void readThis(ByteArrayBuffer reader) {
		super.readThis(reader);
        _systemData.idSystemType(reader.readByte());
        _systemData.idSystemID(reader.readInt());
	}
	
	@Override
	public void writeThis(ByteArrayBuffer writer) {
		super.writeThis(writer);
        writer.writeByte(_systemData.idSystemType());
        writer.writeInt(_systemData.idSystemID());
	}

}
