/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.slots.*;
import com.db4o.io.*;


/**
 * @exclude
 */
public class IoAdaptedObjectContainer extends LocalObjectContainer {

    private final String _fileName;

    private BlockAwareBin          _file;
    
    private volatile BlockAwareBin _backupFile;

    private Object             _fileLock;
    
    private final FreespaceFiller _freespaceFiller;

    IoAdaptedObjectContainer(Configuration config, String fileName) throws OldFormatException {
        super(config,null);
        _fileLock = new Object();
        _fileName = fileName;
        _freespaceFiller=createFreespaceFiller();
        open();
    }

    protected final void openImpl() throws OldFormatException,
			DatabaseReadOnlyException {
		final Storage storage = configImpl().storage();
		boolean isNew = !storage.exists(fileName());
		if (isNew) {
			logMsg(14, fileName());
			checkReadOnly();
			_handlers.oldEncryptionOff();
		}
		
		boolean readOnly = configImpl().isReadOnly();
		boolean lockFile = Debug.lockFile && configImpl().lockFile()
				&& (!readOnly);
		if (needsLockFileThread()) {
			Bin fileBin = storage.open(new BinConfiguration(fileName(), false, 0, false));
			Bin synchronizedBin = new SynchronizedBin(fileBin);
			_file = new BlockAwareBin(synchronizedBin);
		} else {
			_file = new BlockAwareBin(storage.open(new BinConfiguration(fileName(), lockFile, 0, readOnly)));	
		}
		if (isNew) {
			configureNewFile();
			if (configImpl().reservedStorageSpace() > 0) {
				reserve(configImpl().reservedStorageSpace());
			}
			commitTransaction();
			writeHeader(true, false);
		} else {
			readThis();
		}
	}
    
    public void backup(final String path) throws DatabaseClosedException, Db4oIOException {
        withEnvironment(new Runnable() { public void run() {
        	
	    	synchronized (_lock) {
				checkClosed();
				if (_backupFile != null) {
					throw new BackupInProgressException();
				}
				_backupFile = new BlockAwareBin(configImpl().storage().open(new BinConfiguration(path, true,_file.length(), false)));
			}
	        long pos = 0;
	        byte[] buffer = new byte[8192];
	        while (true) {
				synchronized (_lock) {
					int read = _file.read(pos, buffer);
					if (read <= 0) {
						break;
					}
					_backupFile.write(pos, buffer, read);
					pos += read;
				}
			}
	        
			Cool.sleepIgnoringInterruption(1);
	
	        synchronized (_lock) {
				_backupFile.close();
				_backupFile = null;
			}
	        
        }});
    }
    
    public void blockSize(int size){
    	_file.blockSize(size);
    }

    public byte blockSize() {
        return (byte) _file.blockSize();
    }

    protected void freeInternalResources() {
		freePrefetchedPointers();
    }

    protected void shutdownDataStorage() {
		synchronized (_fileLock) {
			try{
				closeFileHeader();
			} finally{
				closeDatabaseFile();
			}
		}
	}

	 /*
     * This method swallows IOException,
     * because it should not affect other close precedures.
     */
	private void closeDatabaseFile() {
		try {
			if (_file != null) {
				_file.close();
			}
		} finally {
			_file = null;
		}
	}
    
    /*
     * This method swallows IOException,
     * because it should not affect other close precedures.
     */
	private void closeFileHeader() {
		try {
			if (_fileHeader != null) {
				_fileHeader.close();
			}
		} finally {
			_fileHeader = null;
		}
	}
	
	@Override
	protected void closeSystemTransaction() {
    	((LocalTransaction)systemTransaction()).close();
	}
    
    public void commit1(Transaction trans) {
        ensureLastSlotWritten();
        super.commit1(trans);
    }

    public void copy(int oldAddress, int oldAddressOffset, int newAddress, int newAddressOffset, int length) {

        if (Debug.xbytes && Deploy.overwrite) {
            checkXBytes(newAddress, newAddressOffset, length);
        }

        try {

            if (_backupFile == null) {
                _file
                    .blockCopy(oldAddress, oldAddressOffset, newAddress, newAddressOffset, length);
                return;
            }

            final byte[] copyBytes = new byte[length];
            _file.blockRead(oldAddress, oldAddressOffset, copyBytes);
            _file.blockWrite(newAddress, newAddressOffset, copyBytes);
            if (_backupFile != null) {
                _backupFile.blockWrite(newAddress, newAddressOffset, copyBytes);
            }

        } catch (Exception e) {
            Exceptions4.throwRuntimeException(16, e);
        }

    }

    private void checkXBytes(int newAddress, int newAddressOffset, int length) {
        if (Debug.xbytes && Deploy.overwrite) {
            try {
                byte[] checkXBytes = new byte[length];
                _file.blockRead(newAddress, newAddressOffset, checkXBytes);
                for (int i = 0; i < checkXBytes.length; i++) {
                    if (checkXBytes[i] != Const4.XBYTE) {
                        String msg = "XByte corruption adress:" + newAddress + " length:"
                            + length + " starting:" + i;
                        throw new Db4oException(msg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public long fileLength() {
        try {
            return _file.length();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public String fileName() {
        return _fileName;
    }


    public void readBytes(byte[] bytes, int address, int length) throws Db4oIOException {
        readBytes(bytes, address, 0, length);
    }

    public void readBytes(byte[] bytes, int address, int addressOffset,
			int length) throws Db4oIOException {

		if (DTrace.enabled) {
			DTrace.READ_BYTES.logLength(address + addressOffset, length);
		}
		int bytesRead = _file.blockRead(address, addressOffset, bytes, length);
		checkReadCount(bytesRead, length);
	}

	private void checkReadCount(int bytesRead, int expected) {
		if (bytesRead != expected) {
			throw new IncompatibleFileFormatException();
		}
	}
	
    public void reserve(int byteCount) throws DatabaseReadOnlyException {
    	checkReadOnly();
        synchronized (_lock) {
        	Slot slot = getSlot(byteCount);
            zeroReservedSlot(slot);
            free(slot);
        }
    }

    private void zeroReservedSlot(Slot slot) {
    	zeroFile(_file, slot);
    	zeroFile(_backupFile, slot);
    }
    
    private void zeroFile(BlockAwareBin io, Slot slot) {
    	if(io == null) {
    		return;
    	}
    	byte[] zeroBytes = new byte[1024];
        int left = slot.length();
        int offset = 0;
        while (left > zeroBytes.length) {
			io.blockWrite(slot.address(), offset, zeroBytes, zeroBytes.length);
			offset += zeroBytes.length;
			left -= zeroBytes.length;
		}
        if(left > 0) {
        	io.blockWrite(slot.address(), offset, zeroBytes, left);
        }
    }

    public void syncFiles() {
        _file.sync();
    }

    public void writeBytes(ByteArrayBuffer buffer, int blockedAddress, int addressOffset) {
		if (Deploy.debug && !Deploy.flush) {
			return;
		}

		if (Debug.xbytes && Deploy.overwrite) {

			boolean doCheck = true;
			if (buffer instanceof StatefulBuffer) {
				StatefulBuffer writer = (StatefulBuffer) buffer;
				if (writer.getID() == Const4.IGNORE_ID) {
					doCheck = false;
				}
			}
			if (doCheck) {
				checkXBytes(blockedAddress, addressOffset, buffer.length());
			}
		}

		if (DTrace.enabled) {
			DTrace.WRITE_BYTES.logLength(blockedAddress + addressOffset, buffer
					.length());
		}

		_file.blockWrite(blockedAddress, addressOffset, buffer._buffer, buffer.length());
		if (_backupFile != null) {
			_backupFile.blockWrite(blockedAddress, addressOffset, buffer._buffer, buffer.length());
		}
	}

    public void overwriteDeletedBytes(int address, int length) {
		if (!Deploy.flush) {
			return;
		}
		if (_freespaceFiller == null) {
			return;
		}
		if (address > 0 && length > 0) {
			if (DTrace.enabled) {
				DTrace.WRITE_XBYTES.logLength(address, length);
			}
			BlockAwareBinWindow window = new BlockAwareBinWindow(_file, address, length);
			try {
				createFreespaceFiller().fill(window);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				window.disable();
			}
		}

	}

	public BlockAwareBin timerFile() {
		return _file;
	}
	
	private FreespaceFiller createFreespaceFiller() {
		if(Debug.xbytes) {
			return new XByteFreespaceFiller();
		}
		return config().freespaceFiller();
	}
	
	private static class XByteFreespaceFiller implements FreespaceFiller {

		public void fill(BlockAwareBinWindow io) throws IOException {
			io.write(0,xBytes(io.length()));
		}

	    private byte[] xBytes(int len) {
	        byte[] bytes = new byte[len];
	        for (int i = 0; i < len; i++) {
	            bytes[i]=Const4.XBYTE;
	        }
	        return bytes;
	    }
	}
}