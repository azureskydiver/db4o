/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.fileheader;

import java.io.IOException;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.internal.*;


/**
 * @exclude
 */
public abstract class FileHeader {
    
    private static final FileHeader[] AVAILABLE_FILE_HEADERS = new FileHeader[]{
        new FileHeader0(),
        new FileHeader1()
    };
    
    private static int readerLength(){
        int length = AVAILABLE_FILE_HEADERS[0].length();
        for (int i = 1; i < AVAILABLE_FILE_HEADERS.length; i++) {
            length = Math.max(length, AVAILABLE_FILE_HEADERS[i].length());
        }
        return length;
    }

    public static FileHeader readFixedPart(LocalObjectContainer file) throws IOException, OldFormatException {
        Buffer reader = prepareFileHeaderReader(file);
        FileHeader header = detectFileHeader(file, reader);
        if(header == null){
            Exceptions4.throwRuntimeException(Messages.INCOMPATIBLE_FORMAT);
        } else {
        	header.readFixedPart(file, reader);
        }
        return header;
    }

	private static Buffer prepareFileHeaderReader(LocalObjectContainer file) {
		Buffer reader = new Buffer(readerLength()); 
        reader.read(file, 0, 0);
		return reader;
	}

	private static FileHeader detectFileHeader(LocalObjectContainer file, Buffer reader) {
        for (int i = 0; i < AVAILABLE_FILE_HEADERS.length; i++) {
            reader.seek(0);
            FileHeader result = AVAILABLE_FILE_HEADERS[i].newOnSignatureMatch(file, reader);
            if(result != null) {
            	return result;
            }
        }
		return null;
	}

    public abstract void close() throws Db4oIOException;

    public abstract void initNew(LocalObjectContainer file) throws Db4oIOException;

    public abstract Transaction interruptedTransaction();

    public abstract int length();
    
    protected abstract FileHeader newOnSignatureMatch(LocalObjectContainer file, Buffer reader);
    
    protected long timeToWrite(long time, boolean shuttingDown) {
    	return shuttingDown ? 0 : time;
    }

    protected abstract void readFixedPart(LocalObjectContainer file, Buffer reader) throws IOException;

    public abstract void readVariablePart(LocalObjectContainer file);
    
    protected boolean signatureMatches(Buffer reader, byte[] signature, byte version){
        for (int i = 0; i < signature.length; i++) {
            if(reader.readByte() != signature[i]){
                return false;
            }
        }
        return reader.readByte() == version; 
    }
    
    // TODO: freespaceID should not be passed here, it should be taken from SystemData
    public abstract void writeFixedPart(
        LocalObjectContainer file, boolean startFileLockingThread, boolean shuttingDown, StatefulBuffer writer, int blockSize, int freespaceID);
    
    public abstract void writeTransactionPointer(Transaction systemTransaction, int transactionAddress);

    protected void writeTransactionPointer(Transaction systemTransaction, int transactionAddress, final int address, final int offset) {
        StatefulBuffer bytes = new StatefulBuffer(systemTransaction, address, Const4.INT_LENGTH * 2);
        bytes.moveForward(offset);
        bytes.writeInt(transactionAddress);
        bytes.writeInt(transactionAddress);
        if (Debug.xbytes && Deploy.overwrite) {
            bytes.setID(Const4.IGNORE_ID);
        }
        bytes.write();
    }
    
    public abstract void writeVariablePart(LocalObjectContainer file, int part);

    protected void readClassCollectionAndFreeSpace(LocalObjectContainer file, Buffer reader) {
        SystemData systemData = file.systemData();
        systemData.classCollectionID(reader.readInt());
        systemData.freespaceID(reader.readInt());
    }

	public static boolean lockedByOtherSession(LocalObjectContainer container, long lastAccessTime) {
		return container.needsLockFileThread() && ( lastAccessTime != 0);
	}


}
