/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;


/**
 * @exclude
 */
public class InMemoryObjectContainer extends LocalObjectContainer {

	private boolean _closed = false;
	private final MemoryFile _memoryFile;
	private int _length = 0;

	protected InMemoryObjectContainer(Configuration config, ObjectContainerBase parent, MemoryFile memoryFile) {
		super(config, parent);
		_memoryFile = memoryFile;
		try {
			open();
		} catch (Exception e) {
			Exceptions4.throwRuntimeException(22, e);
		}
		initialize3();
	}

    public InMemoryObjectContainer(Configuration config, MemoryFile memoryFile) {
        this(config, null, memoryFile);
    }
    
    public void backup(String path) throws IOException{
        Exceptions4.throwRuntimeException(60);
    }
    
    public void blockSize(int size){
        // do nothing, blocksize is always 1
    }

    protected boolean close2() {
        if (Deploy.debug) {
            write(true);
        } else {
            try {
                write(true);
            } catch (Throwable t) {
                fatalException(t);
            }
        }
        super.close2();
        if (!_closed) {
            byte[] temp = new byte[_length];
            System.arraycopy(_memoryFile.getBytes(), 0, temp, 0, _length);
            _memoryFile.setBytes(temp);
        }
        _closed = true;
        return true;
    }

	public void copy(int oldAddress, int oldAddressOffset, int newAddress, int newAddressOffset, int length) {
		int fullNewAddress = newAddress + newAddressOffset;
		ensureMemoryFileSize(fullNewAddress + length);
		byte[] bytes = _memoryFile.getBytes();
		System.arraycopy(bytes, oldAddress + oldAddressOffset, bytes, fullNewAddress, length);
	}

	void emergencyClose() {
        super.emergencyClose();
        _closed = true;
    }

    public long fileLength() {
        return _length;
    }

    String fileName() {
        return "Memory File";
    }

    protected boolean hasShutDownHook() {
        return false;
    }

    public final boolean needsLockFileThread() {
        return false;
    }

    private void open() throws IOException {
        byte[] bytes = _memoryFile.getBytes();
        if (bytes == null || bytes.length == 0) {
            _memoryFile.setBytes(new byte[_memoryFile.getInitialSize()]);
            configureNewFile();
            write(false);
            writeHeader(false);
        } else {
            _length = bytes.length;
            readThis();
        }
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

    public boolean writeAccessTime(int address, int offset, long time) {
        return true;
    }

	public void writeBytes(Buffer bytes, int address, int addressOffset) {
		int fullAddress = address + addressOffset;
		int length = bytes.getLength();
		ensureMemoryFileSize(fullAddress + length);   
		System.arraycopy(bytes._buffer, 0, _memoryFile.getBytes(), fullAddress , length);
	}

    private void ensureMemoryFileSize(int last) {
		if (last < _length) return;
		
		byte[] bytes = _memoryFile.getBytes();
		if (last < bytes.length) {
			_length = last;
			return;
		}

		int increment = _memoryFile.getIncrementSizeBy();
		while (last > increment) {
			increment <<= 1;
		}
		
		byte[] newBytes = new byte[bytes.length + increment];
		System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
		_memoryFile.setBytes(newBytes);
		_length = newBytes.length;
		bytes = null; // hey, GC, kick me hard!
	}

    public void debugWriteXBytes(int a_address, int a_length) {
    }
}
