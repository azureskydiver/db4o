/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.io.*;


/**
 * @exclude
 */
public class IoAdaptedObjectContainer extends LocalObjectContainer {

    private final String _fileName;

    private IoAdapter          _file;
    private IoAdapter          _timerFile;                                 //This is necessary as a separate File because access is not synchronized with access for normal data read/write so the seek pointer can get lost.
    private volatile IoAdapter _backupFile;

    private Object             _fileLock;
    
    private final FreespaceFiller _freespaceFiller;

    IoAdaptedObjectContainer(Configuration config, String fileName) throws OpenDatabaseException, OldFormatException {
        super(config,null);
        _fileLock = new Object();
        _fileName = fileName;
        _freespaceFiller=createFreespaceFiller();
        open();
    }

    protected final void openImpl() throws OpenDatabaseException, OldFormatException {
		IoAdapter ioAdapter = configImpl().ioAdapter();
		boolean isNew = !ioAdapter.exists(fileName());
		if (isNew) {
			logMsg(14, fileName());
			i_handlers.oldEncryptionOff();
		}
		boolean lockFile = Debug.lockFile && configImpl().lockFile()
				&& (!configImpl().isReadOnly());
		try {
			_file = ioAdapter.open(fileName(), lockFile, 0);
			if (needsTimerFile()) {
				_timerFile = ioAdapter.delegatedIoAdapter().open(fileName(),
						false, 0);
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
		} catch (IOException e) {
			throw new OpenDatabaseException(e);
		}
	}
    
    public void backup(String path) throws DatabaseClosedException, BackupException {
        synchronized (i_lock) {
			checkClosed();
			if (_backupFile != null) {
				throw new BackupInProgressException();
			}
			try {
				_backupFile = configImpl().ioAdapter().open(path, true,
						_file.getLength());
			} catch (IOException e) {
				throw new BackupException(e);
			}
			_backupFile.blockSize(blockSize());
		}
        long pos = 0;
        byte[] buffer = new byte[8192];
        while(true){
            synchronized (i_lock) {
				try {
					_file.seek(pos);
					int read = _file.read(buffer);
					if (read <= 0) {
						break;
					}
					_backupFile.seek(pos);
					_backupFile.write(buffer, read);
					pos += read;
				} catch (IOException e) {
					_backupFile = null;
					throw new BackupException(e);
				}
			}
            Cool.sleepIgnoringInterruption(1);
        }

        synchronized (i_lock) {
			try {
				_backupFile.close();
			} catch (IOException e) {
				throw new BackupException(e);
			}
			_backupFile = null;
		}
    }
    
    public void blockSize(int size){
        _file.blockSize(size);
        if (_timerFile != null) {
            _timerFile.blockSize(size);
        }
    }

    public byte blockSize() {
        return (byte) _file.blockSize();
    }

    protected void freeInternalResources() {
		freePrefetchedPointers();
    }

    protected void shutdownDataStorage() {
		synchronized (_fileLock) {
			closeDatabaseFile();
			closeFileHeader();
			closeTimerFile();
		}
	}

	 /*
     * This method swallows IOException,
     * because it should not affect other close precedures.
     */
	private void closeDatabaseFile() {
		try {
			_file.close();
		} catch (IOException e) {
			// ignore
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
			_fileHeader.close();
		} catch (IOException e) {
			// ignore
		} finally {
			_fileHeader = null;
		}
	}
	
	/*
     * This method swallows IOException,
     * because it should not affect other close precedures.
     */
    private void closeTimerFile() {
		try {
			if (_timerFile != null) {
				_timerFile.close();
			}
		} catch (IOException e) {
			// ignore
		} finally {
			_timerFile = null;
		}
	}
    
    public void commit1() {
        ensureLastSlotWritten();
        super.commit1();
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

            byte[] copyBytes = new byte[length];
            _file.blockSeek(oldAddress, oldAddressOffset);
            _file.read(copyBytes);

            _file.blockSeek(newAddress, newAddressOffset);
            _file.write(copyBytes);

            if (_backupFile != null) {
                _backupFile.blockSeek(newAddress, newAddressOffset);
                _backupFile.write(copyBytes);
            }

        } catch (Exception e) {
            Exceptions4.throwRuntimeException(16, e);
        }

    }

    private void checkXBytes(int newAddress, int newAddressOffset, int length) {
        if (Debug.xbytes && Deploy.overwrite) {
            try {
                byte[] checkXBytes = new byte[length];
                _file.blockSeek(newAddress, newAddressOffset);
                _file.read(checkXBytes);
                for (int i = 0; i < checkXBytes.length; i++) {
                    if (checkXBytes[i] != Const4.XBYTE) {
                        String msg = "XByte corruption adress:" + newAddress + " length:"
                            + length;
                        throw new RuntimeException(msg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public long fileLength() {
        try {
            return _file.getLength();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public String fileName() {
        return _fileName;
    }


    public void readBytes(byte[] bytes, int address, int length) throws IOException {
        readBytes(bytes, address, 0, length);
    }

    public void readBytes(byte[] bytes, int address, int addressOffset,
			int length) throws IOException {

		if (DTrace.enabled) {
			DTrace.READ_BYTES.logLength(address + addressOffset, length);
		}
		_file.blockSeek(address, addressOffset);
		int bytesRead = _file.read(bytes, length);
		assertRead(bytesRead, length);
	}

	private void assertRead(int bytesRead, int expected) throws IOException {
		if (bytesRead != expected) {
			throw new IOException("expected read bytes = " + expected
					+ ", but read = " + bytesRead + "bytes");
		}
	}
	
    public void reserve(int byteCount) throws DatabaseReadOnlyException {
    	checkReadOnly();
        synchronized (i_lock) {
            int address = getSlot(byteCount);
            zeroReservedStorage(address, byteCount);
            free(address, byteCount);
        }
    }

    private void zeroReservedStorage(int address, int length) {
        try {
        	zeroFile(_file, address, length);
        	zeroFile(_backupFile, address, length);
        } catch (IOException e) {
            Exceptions4.throwRuntimeException(16, e);
        }
    }
    
    private void zeroFile(IoAdapter io, int address, int length) throws IOException {
    	if(io == null) {
    		return;
    	}
    	byte[] zeroBytes = new byte[1024];
        int left = length;
        io.blockSeek(address, 0);
        while (left > zeroBytes.length) {
			io.write(zeroBytes, zeroBytes.length);
			left -= zeroBytes.length;
		}
        if(left > 0) {
        	io.write(zeroBytes, left);
        }
    }

    public void syncFiles() {
        try {
            _file.sync();
            if (_timerFile != null) {
                _timerFile.sync();
            }
        } catch (Exception e) {
        }
    }

    private boolean needsTimerFile() {
        return needsLockFileThread() && Debug.lockFile;
    }    

    public void writeBytes(Buffer bytes, int address, int addressOffset) {
        if (Deploy.debug && !Deploy.flush) {
            return;
        }

        try {

            if (Debug.xbytes && Deploy.overwrite) {
                
                boolean doCheck = true;
                if(bytes instanceof StatefulBuffer){
                    StatefulBuffer writer = (StatefulBuffer)bytes;
                    if(writer.getID() == Const4.IGNORE_ID){
                        doCheck = false;
                    }
                }
                if (doCheck) {
                    checkXBytes(address, addressOffset, bytes.getLength());
                }
            }

            if (DTrace.enabled) {
                DTrace.WRITE_BYTES.logLength(address + addressOffset,bytes.getLength());
            }

            _file.blockSeek(address, addressOffset);
            _file.write(bytes._buffer, bytes.getLength());
            if (_backupFile != null) {
                _backupFile.blockSeek(address, addressOffset);
                _backupFile.write(bytes._buffer, bytes.getLength());
            }

        } catch (Exception e) {
            Exceptions4.throwRuntimeException(16, e);
        }
    }

    public void overwriteDeletedBytes(int address, int length) {
		if (Deploy.flush) {
		    if (_freespaceFiller!=null) {
		        if(address > 0 && length > 0){
	                if(DTrace.enabled){
	                    DTrace.WRITE_XBYTES.logLength(address, length);
	                }
	                IoAdapterWindow window = new IoAdapterWindow(_file,address,length);
		            try {
						createFreespaceFiller().fill(window);
		                
		            } catch (Exception e) {
		                e.printStackTrace();
		            }
		            finally {
						window.disable();
		            }
		        }
		    }
		}
	}

	public IoAdapter timerFile() {
		return _timerFile;
	}
	
	private FreespaceFiller createFreespaceFiller() {
		FreespaceFiller freespaceFiller=config().freespaceFiller();
		if(Debug.xbytes) {
			freespaceFiller=new XByteFreespaceFiller();
		}
		return freespaceFiller;
	}
	
	private static class XByteFreespaceFiller implements FreespaceFiller {

		public void fill(IoAdapterWindow io) throws IOException {
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