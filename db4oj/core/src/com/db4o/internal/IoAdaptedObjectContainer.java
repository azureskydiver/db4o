/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
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

    IoAdaptedObjectContainer(Configuration config, String fileName) throws Exception {
        super(config,null);
        synchronized (i_lock) {
            _fileLock = new Object();
            _fileName = fileName;
            _freespaceFiller=createFreespaceFiller();
            if (Deploy.debug) {
                // intentionally no Exception handling
                // to find and debug errors
                open();
            } else {
                try {
                    open();
                } catch (DatabaseFileLockedException e) {
                    stopSession();
                    throw e;
                }
            }
            initialize3();
        }
    }

    public void backup(String path) throws IOException {
        synchronized (i_lock) {
            checkClosed();
            if (_backupFile != null) {
                Exceptions4.throwRuntimeException(61);
            }
            try {
                _backupFile = configImpl().ioAdapter().open(path, true, _file.getLength());
                _backupFile.blockSize(blockSize());
            } catch (Exception e) {
                _backupFile = null;
                Exceptions4.throwRuntimeException(12, path);
            }
        }
        long pos = 0;
        int bufferlength = 8192;
        byte[] buffer = new byte[bufferlength];
        while(true){
            synchronized (i_lock) {
                _file.seek(pos);
                int read = _file.read(buffer);
                if(read <= 0 ){
                    break;
                }
                _backupFile.seek(pos);
                _backupFile.write(buffer, read);
                pos += read;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                
            }
        }

        synchronized (i_lock) {
            _backupFile.close();
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

    protected boolean close2() {
		freePrefetchedPointers();
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
		synchronized (_fileLock) {
			try {
				_file.close();
				_file = null;
				_fileHeader.close();
				closeTimerFile();
			} catch (Exception e) {
				_file = null;
				Exceptions4.throwRuntimeException(11, e);
			}
			_file = null;
		}
		return true;
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

    void emergencyClose() {
        super.emergencyClose();
        try {
            _file.close();
        } catch (Exception e) {
        }
        _file = null;
    }

    public long fileLength() {
        try {
            return _file.getLength();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    String fileName() {
        return _fileName;
    }

    private void open() throws Exception {
        boolean isNew = false;
        IoAdapter ioAdapter = configImpl().ioAdapter();
        if (Deploy.debug) {
            if (Deploy.deleteFile) {
                System.out.println("Debug option set to DELETE file.");
                try {
                    ioAdapter.delete(_fileName);
                } catch (Exception e) {
                }
            }
        }
        try {
            if (fileName().length() > 0) {
                                
                if(! ioAdapter.exists(fileName())){
                    isNew = true;
                    logMsg(14, fileName());
                    i_handlers.oldEncryptionOff();
                }
                
                try {
                    boolean lockFile = Debug.lockFile && configImpl().lockFile()
                        && (!configImpl().isReadOnly());
                    _file = ioAdapter.open(fileName(), lockFile, 0);
                    if (needsTimerFile()) {
                        _timerFile = ioAdapter.open(fileName(), false, 0);
                    }
                } catch (DatabaseFileLockedException de) {
                    throw de;
                } catch (Exception e) {
                    Exceptions4.throwRuntimeException(12, fileName(), e);
                }
                if (isNew) {
                    configureNewFile();
                    if (configImpl().reservedStorageSpace() > 0) {
                        reserve(configImpl().reservedStorageSpace());
                    }
                    write(false);
                    writeHeader(true, false);
                } else {
                    readThis();
                }
            } else {
                Exceptions4.throwRuntimeException(21);
            }
        } catch (Exception exc) {
            if (i_references != null) {
                i_references.stopTimer();
            }
            throw exc;
        }
    }

    public void readBytes(byte[] bytes, int address, int length) {
        readBytes(bytes, address, 0, length);
    }

    public void readBytes(byte[] bytes, int address, int addressOffset, int length) {
        
        if (DTrace.enabled) {
            DTrace.READ_BYTES.logLength(address + addressOffset, length);
        }

        try{
            _file.blockSeek(address, addressOffset);
            int bytesRead=_file.read(bytes, length);
            if(bytesRead!=length) {
            	Exceptions4.throwRuntimeException(68, address+"/"+addressOffset,null,false);
            }
        }catch(IOException ioex){
            
            // We need to catch here and throw a runtime exception
            // so the IOException does not need to get declared in 
            // all callers.
            
            // IoExceptions are quite natural to happen if someone
            // mistakenly uses any number as an ID and db4o just
            // interprets as an ID what it reads.
            
            if(Debug.atHome){
                ioex.printStackTrace();
            }
            
            throw new RuntimeException();
        }
    }

    void reserve(int byteCount) {
        synchronized (i_lock) {
            int address = getSlot(byteCount);
            zeroReservedStorage(address, byteCount);
            free(address, byteCount);
        }
    }

    private void zeroReservedStorage(int address, int length) {
        if (configImpl().isReadOnly()) {
            return;
        }
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

    private void closeTimerFile() throws IOException{
        if(_timerFile == null){
            return;
        }
        _timerFile.close();
        _timerFile = null;
    }
    

    public void writeBytes(Buffer bytes, int address, int addressOffset) {
        if (configImpl().isReadOnly()) {
            return;
        }
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
		    if (!configImpl().isReadOnly()&&_freespaceFiller!=null) {
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