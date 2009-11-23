/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.io.*;


/**
 * @exclude
 * @deprecated since 7.11: Use {@link MemoryStorage} instead
 */
public class InMemoryObjectContainer extends LocalObjectContainer {

	private boolean _closed = false;
	private final MemoryFile _memoryFile;
	private int _capacity = 0;
	private int _length = 0;

	public InMemoryObjectContainer(Configuration config, MemoryFile memoryFile)
			throws OldFormatException {
		super(config);
		_memoryFile = memoryFile;
		open();
	}	
	
	protected static final class ConstructionMode {
	}
	
	protected static final ConstructionMode DEFERRED_OPEN_MODE = new ConstructionMode();
	
    protected InMemoryObjectContainer(Config4Impl config, MemoryFile memoryFile, ConstructionMode ignored) {
    	super(config);
    	_memoryFile = memoryFile;
    }
    
    public void deferredOpen() {
		open();
	}

	protected final void openImpl() throws OldFormatException {
		byte[] bytes = _memoryFile.getBytes();
		if (bytes == null || bytes.length == 0) {
			_memoryFile.setBytes(new byte[_memoryFile.getInitialSize()]);
			configureNewFile();
			commitTransaction();
			writeHeader(false, false);
		} else {
			_length = _capacity = bytes.length;
			readThis();
		}
	}
    
    public void backup(Storage targetStorage, String path) throws NotSupportedException {
        throw new NotSupportedException();
    }

    public void blockSize(int size){
        // do nothing, blocksize is always 1
    }
    
    @Override
    protected void closeTransaction() {
    	// do nothing
    }
    
    @Override
    protected void closeSystemTransaction() {
    	// do nothing
    }

    protected void shutdownDataStorage() {
		if (!_closed) {
			byte[] temp = new byte[_capacity];
			System.arraycopy(_memoryFile.getBytes(), 0, temp, 0, _capacity);
			_memoryFile.setBytes(temp);
		}
		_closed = true;
		dropReferences();
	}
    
    protected void dropReferences() {
    	// do nothing
    }

    public long fileLength() {
        return _length;
    }

    public String fileName() {
        return "Memory File";
    }

    protected boolean hasShutDownHook() {
        return false;
    }

    public final boolean needsLockFileThread() {
        return false;
    }

	public void readBytes(byte[] bytes, int address, int length) {
		try {
			System.arraycopy(_memoryFile.getBytes(), address, bytes, 0, length);
		} catch (Exception e) {
			Exceptions4.throwRuntimeException(13, e);
		}
	}

	public void readBytes(byte[] bytes, int address, int addressOffset, int length){
		readBytes(bytes, address + addressOffset, length);
	}

    public void syncFiles() {
    }

	public void writeBytes(ByteArrayBuffer buffer, int address, int addressOffset) {
		int fullAddress = address + addressOffset;
		int length = buffer.length();
		ensureMemoryFileSize(fullAddress + length);   
		System.arraycopy(buffer._buffer, 0, _memoryFile.getBytes(), fullAddress , length);
		_length = Math.max(_length, fullAddress + length + 1);
	}

    private void ensureMemoryFileSize(int last) {
		if (last < _capacity) return;
		
		byte[] bytes = _memoryFile.getBytes();
		if (last < bytes.length) {
			_capacity = last;
			return;
		}
		
		int increment = _memoryFile.getIncrementSizeBy();
        while (last > (increment + bytes.length)) {
        	increment <<= 1;
        }
		
		byte[] newBytes = new byte[bytes.length + increment];
		System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
		_memoryFile.setBytes(newBytes);
		_capacity = newBytes.length;
	}

    public void overwriteDeletedBytes(int a_address, int a_length) {
    }

	public void reserve(int byteCount) {
		throw new NotSupportedException();
	}

	public byte blockSize() {
		return 1;
	}
	
	@Override
	protected void fatalStorageShutdown() {
		shutdownDataStorage();
	}
}
