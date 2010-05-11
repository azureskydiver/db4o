/* Copyright (C) 2004 - 2006  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.fileheader;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.internal.*;

/**
 * @exclude
 */
public class FileHeader0 extends FileHeader {
    
    static final int HEADER_LENGTH = 2 + (Const4.INT_LENGTH * 4);

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
    
    public FileHeader0(LocalObjectContainer container){
    	
    }
    

    public void close() throws Db4oIOException {
        _configBlock.close();
    }
    
    protected FileHeader newOnSignatureMatch(LocalObjectContainer file, ByteArrayBuffer reader) {
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
        return new FileHeader0(file);
    }

    
    protected void read(LocalObjectContainer file, ByteArrayBuffer reader) throws OldFormatException {
        _configBlock = ConfigBlock.forExistingFile(file, reader.readInt());
        reader.incrementOffset(Const4.ID_LENGTH);
        SystemData systemData = file.systemData();
		systemData.classCollectionID(reader.readInt());
		reader.readInt();  // was freespace ID, can no longer be read
    }

    private Object getBootRecord(LocalObjectContainer file) {
		file.showInternalClasses(true);
		try {
			return file.getByID(file.systemTransaction(), _configBlock._bootRecordID);
		} finally {
			file.showInternalClasses(false);
		}
	}

    public void initNew(LocalObjectContainer file) throws Db4oIOException {
    	throw new IllegalStateException();
    }
    
    public void completeInterruptedTransaction(LocalObjectContainer container) {
        _configBlock.completeInterruptedTransaction();
    }

    public void writeTransactionPointer(Transaction systemTransaction, int transactionPointer) {
        writeTransactionPointer(systemTransaction, transactionPointer, _configBlock.address(), ConfigBlock.TRANSACTION_OFFSET);
    }

    public MetaIndex getUUIDMetaIndex() {
        return _bootRecord.getUUIDMetaIndex();
    }

    public int length(){
        return HEADER_LENGTH;
    }

    public void writeFixedPart(LocalObjectContainer file, boolean startFileLockingThread, boolean shuttingDown, StatefulBuffer writer, int blockSize_) {
    	throw new IllegalStateException();
    }
    
    public void writeVariablePart(LocalObjectContainer file, boolean shuttingDown) {
    	throw new IllegalStateException();
    }

	@Override
	public void readIdentity(LocalObjectContainer container) {
        if (_configBlock._bootRecordID <= 0) {
            return;
        }
        Object bootRecord = getBootRecord(container);
        
        if (! (bootRecord instanceof PBootRecord)) {
            return;
        }
        
        _bootRecord = (PBootRecord) bootRecord;
        container.activate(bootRecord, Integer.MAX_VALUE);
        container.setNextTimeStampId(_bootRecord.i_versionGenerator);
        
        container.systemData().identity(_bootRecord.i_db);
	}

	@Override
	public Runnable commit(boolean shuttingDown) {
		throw new IllegalStateException();
	}

}
