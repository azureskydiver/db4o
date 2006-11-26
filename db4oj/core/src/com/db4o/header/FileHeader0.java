/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.header;

import java.io.*;

import com.db4o.*;

/**
 * @exclude
 */
public class FileHeader0 extends FileHeader {
    
    static final int LENGTH = 2 + (YapConst.INT_LENGTH * 4);

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

    
    private YapConfigBlock    _configBlock;
    
    private PBootRecord _bootRecord;
    

    public void close() throws IOException {
        _configBlock.close();
    }
    
    protected FileHeader newOnSignatureMatch(YapFile file, YapReader reader) {
        byte firstFileByte = reader.readByte();
        if (firstFileByte != YapConst.YAPBEGIN) {
            if(firstFileByte != YapConst.YAPFILEVERSION){
                return null;
            }
            file.blockSizeReadFromFile(reader.readByte());
        }else{
            if (reader.readByte() != YapConst.YAPFILE) {
                return null;
            }
        }
        return new FileHeader0();
    }

    
    protected void readFixedPart(YapFile file, YapReader reader) throws IOException {
        _configBlock = YapConfigBlock.forExistingFile(file, reader.readInt());
        skipConfigurationLockTime(reader);
        readClassCollectionAndFreeSpace(file, reader);
    }

    private void skipConfigurationLockTime(YapReader reader) {
        reader.incrementOffset(YapConst.ID_LENGTH);
    }

    public void readVariablePart(YapFile file){
        if (_configBlock._bootRecordID <= 0) {
            return;
        }
        file.showInternalClasses(true);
        Object bootRecord = file.getByID1(file.getSystemTransaction(), _configBlock._bootRecordID);
        file.showInternalClasses(false);
        
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

    public void initNew(YapFile file) throws IOException {
        _configBlock = YapConfigBlock.forNewFile(file);
        initBootRecord(file);
    }
    
    private void initBootRecord(YapFile file){
        
        file.showInternalClasses(true);
        
        _bootRecord = new PBootRecord();
        file.setInternal(file.getSystemTransaction(), _bootRecord, false);
        
        _configBlock._bootRecordID = file.getID1(_bootRecord);
        writeVariablePart(file, 1);
        
        file.showInternalClasses(false);
    }

    public Transaction interruptedTransaction() {
        return _configBlock.getTransactionToCommit();
    }

    public void writeTransactionPointer(Transaction systemTransaction, int transactionAddress) {
        writeTransactionPointer(systemTransaction, transactionAddress, _configBlock.address(), YapConfigBlock.TRANSACTION_OFFSET);
    }

    public MetaIndex getUUIDMetaIndex() {
        return _bootRecord.getUUIDMetaIndex();
    }

    public int length(){
        return LENGTH;
    }

    public void writeFixedPart(YapFile file, boolean shuttingDown, YapWriter writer, int blockSize_, int freespaceID) {
        writer.append(YapConst.YAPFILEVERSION);
        writer.append((byte)blockSize_);
        writer.writeInt(_configBlock.address());
        writer.writeInt((int)timeToWrite(_configBlock.openTime(), shuttingDown));
        writer.writeInt(file.systemData().classCollectionID());
        writer.writeInt(freespaceID);
        if (Debug.xbytes && Deploy.overwrite) {
            writer.setID(YapConst.IGNORE_ID);
        }
        writer.write();
    }
    
    public void writeVariablePart(YapFile file, int part) {
        if(part == 1){
            _configBlock.write();
        }else if(part == 2){
            _bootRecord.write(file);
        }
    }

}
