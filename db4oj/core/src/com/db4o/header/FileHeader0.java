/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.header;

import java.io.*;

import com.db4o.*;
import com.db4o.inside.*;
import com.db4o.inside.convert.*;
import com.db4o.io.*;


public class FileHeader0 {
    
    protected YapConfigBlock    _configBlock;
    
    
    // FIXME: All the variables here should always be valid
    //        or removed from here. Right now some of the variables
    //        are only used for reading, they are invalid on 
    //        creating new files
    
    private byte blockSize = 1;
    
    private int _classCollectionID;
    
    private int _freeSpaceID;
    
    PBootRecord _bootRecord;

    public void read0(YapReader reader) {
        
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
    }

    public byte blockSize() {
        return blockSize;
    }

    public void readConfigBlock(YapFile yapFile, YapReader reader) {
        
        _configBlock = new YapConfigBlock(yapFile);
        
        _configBlock.read(reader.readInt());

        // configuration lock time skipped
        reader.incrementOffset(YapConst.YAPID_LENGTH);
        
        _classCollectionID = reader.readInt();
        
        _freeSpaceID = reader.readInt();
        
    }
    
    public void readBootRecord(YapFile yapFile){
        
        Object bootRecord = null;
        if (_configBlock._bootRecordID > 0) {
            yapFile.showInternalClasses(true);
            bootRecord = yapFile.getByID1(yapFile.getSystemTransaction(), _configBlock._bootRecordID);
            yapFile.showInternalClasses(false);
        }
        if (bootRecord instanceof PBootRecord) {
            _bootRecord = (PBootRecord) bootRecord;
        }
        
    }

    public int classCollectionID() {
        return _classCollectionID;
    }

    public int freeSpaceID() {
        return _freeSpaceID;
    }

    public byte freespaceSystem() {
        return _configBlock._freespaceSystem;
    }

    public static FileHeader0 forNewFile(YapFile yf) {
        FileHeader0 fh = new FileHeader0();
        fh.initNew(yf);
        return fh;
    }

    private void initNew(YapFile yf) {
        _configBlock = new YapConfigBlock(yf);
        _configBlock.converterVersion(Converter.VERSION);
        _configBlock.write();
        _configBlock.go();
    }

    public int freespaceAddress() {
        return _configBlock._freespaceAddress;
    }

    public void setBootRecordID(int id) {
        _configBlock._bootRecordID = id;
        _configBlock.write();
    }

    public int newFreespaceSlot(byte freeSpaceSystem) {
        return _configBlock.newFreespaceSlot(freeSpaceSystem);
    }

    public void writeVariablePart() {
        _configBlock.write();
    }

    public PBootRecord bootRecord() {
        return _bootRecord;
    }

    public Transaction interruptedTransaction() {
        return _configBlock.getTransactionToCommit();
    }

    public int converterVersion() {
        return _configBlock.converterVersion();
    }

    public void converterVersion(int version) {
        _configBlock.converterVersion(version);
    }

    public int transactionPointerAddress() {
        return _configBlock._address;
    }

    public void writeTransactionPointer(Transaction trans, int address) {
        YapWriter bytes = new YapWriter(trans,
            _configBlock._address, YapConst.YAPINT_LENGTH * 2);
        
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

    public void writeFixedPart(YapWriter writer, byte blockSize, int classCollectionID, int freespaceID) {
        writer.append(YapConst.YAPFILEVERSION);
        writer.append(blockSize);
        writer.writeInt(  _configBlock._address);
        writer.writeInt(0);
        writer.writeInt(classCollectionID);
        writer.writeInt(freespaceID);
        if (Debug.xbytes && Deploy.overwrite) {
            writer.setID(YapConst.IGNORE_ID);
        }
        writer.write();
    }


}
