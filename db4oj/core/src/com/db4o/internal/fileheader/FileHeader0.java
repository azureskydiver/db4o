/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.fileheader;

import java.io.*;

import com.db4o.*;
import com.db4o.internal.*;

/**
 * @exclude
 */
public class FileHeader0 extends FileHeader {
    
    static final int LENGTH = 2 + (Const4.INT_LENGTH * 4);

    // The header format is:

    // Old format
    // -------------------------
    // {
    // Y
    // [Rest]

    
    // New format
    // -------------------------
    // (byte)4
    // block size in bytes 1 to 127
    // [Rest]
    

    // Rest (only ints)
    // -------------------
    // address of the extended configuration block, see YapConfigBlock
    // headerLock
    // YapClassCollection ID
    // FreeBySize ID

    
    private ConfigBlock    _configBlock;
    
    private PBootRecord _bootRecord;
    

    public void close() throws IOException {
        _configBlock.close();
    }
    
    protected FileHeader newOnSignatureMatch(LocalObjectContainer file, Buffer reader) {
        byte firstFileByte = reader.readByte();
        if (firstFileByte != Const4.YAPBEGIN) {
            if(firstFileByte != Const4.YAPFILEVERSION){
                return null;
            }
            file.blockSizeReadFromFile(reader.readByte());
        }else{
            if (reader.readByte() != Const4.YAPFILE) {
                return null;
            }
        }
        return new FileHeader0();
    }

    
    protected void readFixedPart(LocalObjectContainer file, Buffer reader) throws IOException {
        _configBlock = ConfigBlock.forExistingFile(file, reader.readInt());
        skipConfigurationLockTime(reader);
        readClassCollectionAndFreeSpace(file, reader);
    }

    private void skipConfigurationLockTime(Buffer reader) {
        reader.incrementOffset(Const4.ID_LENGTH);
    }

    public void readVariablePart(LocalObjectContainer file){
        if (_configBlock._bootRecordID <= 0) {
            return;
        }
        Object bootRecord = getBootRecord(file);
        
        if (! (bootRecord instanceof PBootRecord)) {
            initBootRecord(file);
            file.generateNewIdentity();
            return;
        }
        
        _bootRecord = (PBootRecord) bootRecord;
        file.activate(bootRecord, Integer.MAX_VALUE);
        file.setNextTimeStampId(_bootRecord.i_versionGenerator);
        
        file.systemData().identity(_bootRecord.i_db);
    }

	private Object getBootRecord(LocalObjectContainer file) {
		file.showInternalClasses(true);
		try {
			return file.getByID1(file.getSystemTransaction(), _configBlock._bootRecordID);
		} finally {
			file.showInternalClasses(false);
		}
	}

    public void initNew(LocalObjectContainer file) throws IOException {
        _configBlock = ConfigBlock.forNewFile(file);
        initBootRecord(file);
    }
    
    private void initBootRecord(LocalObjectContainer file){
        
        file.showInternalClasses(true);
        try {
	        _bootRecord = new PBootRecord();
	        file.setInternal(file.getSystemTransaction(), _bootRecord, false);
	        
	        _configBlock._bootRecordID = file.getID1(_bootRecord);
	        writeVariablePart(file, 1);
        } finally {
        	file.showInternalClasses(false);
        }
    }

    public Transaction interruptedTransaction() {
        return _configBlock.getTransactionToCommit();
    }

    public void writeTransactionPointer(Transaction systemTransaction, int transactionAddress) {
        writeTransactionPointer(systemTransaction, transactionAddress, _configBlock.address(), ConfigBlock.TRANSACTION_OFFSET);
    }

    public MetaIndex getUUIDMetaIndex() {
        return _bootRecord.getUUIDMetaIndex();
    }

    public int length(){
        return LENGTH;
    }

    public void writeFixedPart(LocalObjectContainer file, boolean startFileLockingThread, boolean shuttingDown, StatefulBuffer writer, int blockSize_, int freespaceID) {
        writer.append(Const4.YAPFILEVERSION);
        writer.append((byte)blockSize_);
        writer.writeInt(_configBlock.address());
        writer.writeInt((int)timeToWrite(_configBlock.openTime(), shuttingDown));
        writer.writeInt(file.systemData().classCollectionID());
        writer.writeInt(freespaceID);
        if (Debug.xbytes && Deploy.overwrite) {
            writer.setID(Const4.IGNORE_ID);
        }
        writer.write();
    }
    
    public void writeVariablePart(LocalObjectContainer file, int part) {
        if(part == 1){
            _configBlock.write();
        }else if(part == 2){
            _bootRecord.write(file);
        }
    }

}
