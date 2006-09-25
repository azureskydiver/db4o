/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.header;

import java.io.*;

import com.db4o.*;
import com.db4o.inside.*;
import com.db4o.inside.convert.*;
import com.db4o.io.*;

/**
 * @exclude
 */
public class FileHeader0 extends FileHeader {
    
    private static final int LENGTH = 2 + (YapConst.INT_LENGTH * 4);

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
    
    private byte blockSize = 1;
    
    private PBootRecord _bootRecord;

    private final SystemData _systemData;
    
    public FileHeader0(SystemData systemData){
        _systemData = systemData;
    }

    public void read(YapFile file) {
        
        YapReader reader = new YapReader(length()); 
        reader.read(file, 0, 0);
        
        byte firstFileByte = reader.readByte();
        if (firstFileByte != YapConst.YAPBEGIN) {
            if(firstFileByte != YapConst.YAPFILEVERSION){
                Exceptions4.throwRuntimeException(17);
            }
            blockSize = reader.readByte();
        }else{
            if (reader.readByte() != YapConst.YAPFILE) {
                Exceptions4.throwRuntimeException(17);
            }
        }
        
        file.blockSize(blockSize);
        file.setRegularEndAddress(file.fileLength());
        
        _configBlock = new YapConfigBlock(file);
        
        _configBlock.read(_systemData, reader.readInt());

        skipConfigurationLockTime(reader);
        
        _systemData.classCollectionID(reader.readInt());
        _systemData.freespaceID(reader.readInt());
    }

    private void skipConfigurationLockTime(YapReader reader) {
        reader.incrementOffset(YapConst.ID_LENGTH);
    }

    public void readVariablePart2(YapFile yapFile){
        if (_configBlock._bootRecordID <= 0) {
            return;
        }
        yapFile.showInternalClasses(true);
        Object bootRecord = yapFile.getByID1(yapFile.getSystemTransaction(), _configBlock._bootRecordID);
        yapFile.showInternalClasses(false);
        
        if (! (bootRecord instanceof PBootRecord)) {
            initBootRecord(yapFile);
            yapFile.generateNewIdentity();
            return;
        }
        
        _bootRecord = (PBootRecord) bootRecord;
        _bootRecord.i_stream = yapFile;
        yapFile.activate(bootRecord, Integer.MAX_VALUE);
        yapFile.setNextTimeStampId(_bootRecord.i_versionGenerator);
        
        _systemData.identity(_bootRecord.i_db);
    }

    public void initNew(YapFile yf) {
        _systemData.converterVersion(Converter.VERSION);
        _configBlock = new YapConfigBlock(yf);
        _configBlock.go();
        initBootRecord(yf);
    }
    
    private void initBootRecord(YapFile yf){
        
        yf.showInternalClasses(true);
        
        _bootRecord = new PBootRecord();
        _bootRecord.i_stream = yf;
        _bootRecord.init();
        
        yf.setInternal(yf.getSystemTransaction(), _bootRecord, false);
        
        _configBlock._bootRecordID = yf.getID1(yf.getSystemTransaction(), _bootRecord);
        writeVariablePart1();
        
        yf.showInternalClasses(false);
    }

    public Transaction interruptedTransaction() {
        return _configBlock.getTransactionToCommit();
    }

    public int transactionPointerAddress() {
        return _configBlock._address;
    }

    public void writeTransactionPointer(Transaction trans, int address) {
        YapWriter bytes = new YapWriter(trans,
            _configBlock._address, YapConst.INT_LENGTH * 2);
        
        bytes.moveForward(YapConfigBlock.TRANSACTION_OFFSET);
        
        bytes.writeInt(address);
        bytes.writeInt(address);
        if (Debug.xbytes && Deploy.overwrite) {
            bytes.setID(YapConst.IGNORE_ID);
        }
        bytes.write();
    }

    public void seekForTimeLock(IoAdapter file) throws IOException {
        file.blockSeek(_configBlock._address, YapConfigBlock.ACCESS_TIME_OFFSET);
    }

    public MetaIndex getUUIDMetaIndex() {
        return _bootRecord.getUUIDMetaIndex();
    }

    public int length(){
        return LENGTH;
    }

    public void writeFixedPart(boolean shuttingDown, YapWriter writer, byte blockSize_, int classCollectionID, int freespaceID) {
        writer.append(YapConst.YAPFILEVERSION);
        writer.append(blockSize_);
        writer.writeInt(_configBlock._address);
        
        int headerLockOpenTime = shuttingDown ? 0 : (int)_configBlock._opentime;  
        writer.writeInt(headerLockOpenTime);
        
        writer.writeInt(classCollectionID);
        writer.writeInt(freespaceID);
        if (Debug.xbytes && Deploy.overwrite) {
            writer.setID(YapConst.IGNORE_ID);
        }
        writer.write();
    }

    public void writeVariablePart1() {
        _configBlock.write(_systemData);
    }
    
    public void writeVariablePart2() {
        _bootRecord.write(_systemData);
    }

}
