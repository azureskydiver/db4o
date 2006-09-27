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
    
    private PBootRecord _bootRecord;

    public void read(YapFile file) throws IOException {
        
        YapReader reader = new YapReader(length()); 
        reader.read(file, 0, 0);
     
        byte blockSize = 1;
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
        
        SystemData systemData = file.systemData();

        _configBlock.read(systemData, reader.readInt());
        startTimerFileLock(file);

        skipConfigurationLockTime(reader);
        
        systemData.classCollectionID(reader.readInt());
        systemData.freespaceID(reader.readInt());
    }

    private void skipConfigurationLockTime(YapReader reader) {
        reader.incrementOffset(YapConst.ID_LENGTH);
    }

    public void readVariablePart2(YapFile file){
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
        file.systemData().converterVersion(Converter.VERSION);
        _configBlock = new YapConfigBlock(file);
        startTimerFileLock(file);
        initBootRecord(file);
    }
    
    private void startTimerFileLock(YapFile file) throws IOException{
        file.systemData().timerFileLock().start();
    }
    
    private void initBootRecord(YapFile file){
        
        file.showInternalClasses(true);
        
        _bootRecord = new PBootRecord();
        file.setInternal(file.getSystemTransaction(), _bootRecord, false);
        
        _configBlock._bootRecordID = file.getID1(file.getSystemTransaction(), _bootRecord);
        writeVariablePart1(file);
        
        file.showInternalClasses(false);
    }

    public Transaction interruptedTransaction() {
        return _configBlock.getTransactionToCommit();
    }

    public int transactionPointerAddress() {
        return _configBlock.address();
    }

    public void writeTransactionPointer(Transaction trans, int address) {
        YapWriter bytes = new YapWriter(trans,
            _configBlock.address(), YapConst.INT_LENGTH * 2);
        
        bytes.moveForward(YapConfigBlock.TRANSACTION_OFFSET);
        
        bytes.writeInt(address);
        bytes.writeInt(address);
        if (Debug.xbytes && Deploy.overwrite) {
            bytes.setID(YapConst.IGNORE_ID);
        }
        bytes.write();
    }

    public boolean seekForTimeLock(IoAdapter file) throws IOException {
        if(_configBlock.address() == 0){
            return false;
        }
        file.blockSeek(_configBlock.address(), YapConfigBlock.ACCESS_TIME_OFFSET);
        return true;
    }

    public MetaIndex getUUIDMetaIndex() {
        return _bootRecord.getUUIDMetaIndex();
    }

    public int length(){
        return LENGTH;
    }

    public void writeFixedPart(YapFile file, boolean shuttingDown, YapWriter writer, byte blockSize_, int classCollectionID, int freespaceID) {
        writer.append(YapConst.YAPFILEVERSION);
        writer.append(blockSize_);
        writer.writeInt(_configBlock.address());
        writer.writeInt(openTimeToWrite(file, shuttingDown));
        writer.writeInt(classCollectionID);
        writer.writeInt(freespaceID);
        if (Debug.xbytes && Deploy.overwrite) {
            writer.setID(YapConst.IGNORE_ID);
        }
        writer.write();
    }
    
    private int openTimeToWrite(YapFile file, boolean shuttingDown){
        return shuttingDown ? 0 : (int)file.systemData().timerFileLock().openTime();
    }

    public void writeVariablePart1(YapFile file) {
        _configBlock.write(file.systemData());
    }
    
    public void writeVariablePart2(YapFile file) {
        _bootRecord.write(file);
    }

}
