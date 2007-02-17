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

    private IoAdapter          i_file;
    private IoAdapter          i_timerFile;                                 //This is necessary as a separate File because access is not synchronized with access for normal data read/write so the seek pointer can get lost.
    private volatile IoAdapter i_backupFile;

    private Object             i_fileLock;

    IoAdaptedObjectContainer(Configuration config, String fileName) throws Exception {
        super(config,null);
        synchronized (i_lock) {
            i_fileLock = new Object();
            _fileName = fileName;
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
            if (i_backupFile != null) {
                Exceptions4.throwRuntimeException(61);
            }
            try {
                i_backupFile = configImpl().ioAdapter().open(path, true, i_file.getLength());
                i_backupFile.blockSize(blockSize());
            } catch (Exception e) {
                i_backupFile = null;
                Exceptions4.throwRuntimeException(12, path);
            }
        }
        long pos = 0;
        int bufferlength = 8192;
        byte[] buffer = new byte[bufferlength];
        while(true){
            synchronized (i_lock) {
                i_file.seek(pos);
                int read = i_file.read(buffer);
                if(read <= 0 ){
                    break;
                }
                i_backupFile.seek(pos);
                i_backupFile.write(buffer, read);
                pos += read;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                
            }
        }

        synchronized (i_lock) {
            i_backupFile.close();
            i_backupFile = null;
        }
    }
    
    public void blockSize(int size){
        i_file.blockSize(size);
        if (i_timerFile != null) {
            i_timerFile.blockSize(size);
        }
    }

    public byte blockSize() {
        return (byte) i_file.blockSize();
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
		synchronized (i_fileLock) {
			try {
				i_file.close();
				i_file = null;
				_fileHeader.close();
				closeTimerFile();
			} catch (Exception e) {
				i_file = null;
				Exceptions4.throwRuntimeException(11, e);
			}
			i_file = null;
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

            if (i_backupFile == null) {
                i_file
                    .blockCopy(oldAddress, oldAddressOffset, newAddress, newAddressOffset, length);
                return;
            }

            byte[] copyBytes = new byte[length];
            i_file.blockSeek(oldAddress, oldAddressOffset);
            i_file.read(copyBytes);

            i_file.blockSeek(newAddress, newAddressOffset);
            i_file.write(copyBytes);

            if (i_backupFile != null) {
                i_backupFile.blockSeek(newAddress, newAddressOffset);
                i_backupFile.write(copyBytes);
            }

        } catch (Exception e) {
            Exceptions4.throwRuntimeException(16, e);
        }

    }

    private void checkXBytes(int a_newAddress, int newAddressOffset, int a_length) {
        if (Debug.xbytes && Deploy.overwrite) {
            try {
                byte[] checkXBytes = new byte[a_length];
                i_file.blockSeek(a_newAddress, newAddressOffset);
                i_file.read(checkXBytes);
                for (int i = 0; i < checkXBytes.length; i++) {
                    if (checkXBytes[i] != Const4.XBYTE) {
                        String msg = "XByte corruption adress:" + a_newAddress + " length:"
                            + a_length;
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
            i_file.close();
        } catch (Exception e) {
        }
        i_file = null;
    }

    public long fileLength() {
        try {
            return i_file.getLength();
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
                    i_file = ioAdapter.open(fileName(), lockFile, 0);
                    if (needsTimerFile()) {
                        i_timerFile = ioAdapter.open(fileName(), false, 0);
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
            i_file.blockSeek(address, addressOffset);
            int bytesRead=i_file.read(bytes, length);
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
        	zeroFile(i_file, address, length);
        	zeroFile(i_backupFile, address, length);
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
            i_file.sync();
            if (i_timerFile != null) {
                i_timerFile.sync();
            }
        } catch (Exception e) {
        }
    }

    private boolean needsTimerFile() {
        return needsLockFileThread() && Debug.lockFile;
    }

    private void closeTimerFile() throws IOException{
        if(i_timerFile == null){
            return;
        }
        i_timerFile.close();
        i_timerFile = null;
    }
    

    public void writeBytes(Buffer a_bytes, int address, int addressOffset) {
        if (configImpl().isReadOnly()) {
            return;
        }
        if (Deploy.debug && !Deploy.flush) {
            return;
        }

        try {

            if (Debug.xbytes && Deploy.overwrite) {
                
                boolean doCheck = true;
                if(a_bytes instanceof StatefulBuffer){
                    StatefulBuffer writer = (StatefulBuffer)a_bytes;
                    if(writer.getID() == Const4.IGNORE_ID){
                        doCheck = false;
                    }
                }
                if (doCheck) {
                    checkXBytes(address, addressOffset, a_bytes.getLength());
                }
            }

            if (DTrace.enabled) {
                DTrace.WRITE_BYTES.logLength(address + addressOffset,a_bytes.getLength());
            }

            i_file.blockSeek(address, addressOffset);
            i_file.write(a_bytes._buffer, a_bytes.getLength());
            if (i_backupFile != null) {
                i_backupFile.blockSeek(address, addressOffset);
                i_backupFile.write(a_bytes._buffer, a_bytes.getLength());
            }

        } catch (Exception e) {
            Exceptions4.throwRuntimeException(16, e);
        }
    }

    public void debugWriteXBytes(int a_address, int a_length) {
        if (Debug.xbytes) {
            writeXBytes(a_address, a_length);
        }
    }

	public void writeXBytes(int a_address, int a_length) {
		if (Deploy.flush) {
		    if (!configImpl().isReadOnly()) {
		        if(a_address > 0 && a_length > 0){
		            try {
		                if(DTrace.enabled){
		                    DTrace.WRITE_XBYTES.logLength(a_address, a_length);
		                }
		                i_file.blockSeek(a_address);
		                i_file.write(xBytes(a_address, a_length)._buffer, a_length);
		                
		            } catch (Exception e) {
		                e.printStackTrace();
		            }
		        }
		    }
		}
	}

	public IoAdapter timerFile() {
		return i_timerFile;
	}
}