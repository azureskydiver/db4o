/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.io.*;

/**
 * @exclude
 */
public class YapRandomAccessFile extends YapFile {

    private Session            i_session;

    private IoAdapter          i_file;
    private IoAdapter          i_timerFile;                                 //This is necessary as a separate File because access is not synchronized with access for normal data read/write so the seek pointer can get lost.
    private volatile IoAdapter i_backupFile;
    private byte[]             i_timerBytes = new byte[YapConst.LONG_BYTES];

    private Object             i_fileLock;

    YapRandomAccessFile(Session a_session) throws Exception {
        super(null);
        synchronized (i_lock) {
            i_fileLock = new Object();
            i_session = a_session;
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
                Db4o.throwRuntimeException(61);
            }
            try {
                i_backupFile = i_config.i_ioAdapter.open(path, true, i_file.getLength());
            } catch (Exception e) {
                i_backupFile = null;
                Db4o.throwRuntimeException(12, path);
            }
        }
        long pos = 0;
        int bufferlength = 8192;
        byte[] buffer = new byte[bufferlength];
        do {
            synchronized (i_lock) {
                i_file.seek(pos);
                int read = i_file.read(buffer);
                i_backupFile.seek(pos);
                i_backupFile.write(buffer, read);
                pos += read;
                i_lock.notify();
            }
        } while (pos < i_file.getLength());
        synchronized (i_lock) {
            i_backupFile.close();
            i_backupFile = null;
        }
    }

    void blockSize(int blockSize) {
        i_file.blockSize(blockSize);
        if (i_timerFile != null) {
            i_timerFile.blockSize(blockSize);
        }
    }

    byte blockSize() {
        return (byte) i_file.blockSize();
    }

    boolean close2() {
        boolean stopSession = true;
        synchronized (Db4o.lock) {
            stopSession = i_session.closeInstance();
            if (stopSession) {
                freePrefetchedPointers();
                i_entryCounter++;
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
                i_entryCounter--;
                Db4o.sessionStopped(i_session);
                synchronized (i_fileLock) {
                    try {
                        i_file.close();
                        i_file = null;
                        if (needsLockFileThread() && Debug.lockFile) {
                            YapWriter lockBytes = new YapWriter(i_systemTrans,
                                YapConst.YAPLONG_LENGTH);
                            YLong.writeLong(0, lockBytes);
                            i_timerFile.blockSeek(i_configBlock._address,
                                YapConfigBlock.ACCESS_TIME_OFFSET);
                            i_timerFile.write(lockBytes._buffer);
                            i_timerFile.close();
                        }
                    } catch (Exception e) {
                        i_file = null;
                        Db4o.throwRuntimeException(11, e);
                    }
                    i_file = null;
                }
            }
        }
        return stopSession;
    }

    public void copy(int oldAddress, int oldAddressOffset, int newAddress, int newAddressOffset, int length) {

        if (Deploy.debug && Deploy.overwrite) {
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
            Db4o.throwRuntimeException(16, e);
        }

    }

    private void checkXBytes(int a_newAddress, int newAddressOffset, int a_length) {
        if (Deploy.debug && Deploy.overwrite) {
            try {
                byte[] checkXBytes = new byte[a_length];
                i_file.blockSeek(a_newAddress, newAddressOffset);
                i_file.read(checkXBytes);
                for (int i = 0; i < checkXBytes.length; i++) {
                    if (checkXBytes[i] != YapConst.XBYTE) {
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
        try {
            Db4o.sessionStopped(i_session);
        } catch (Exception e) {
        }
        i_file = null;
    }

    long fileLength() {
        try {
            return i_file.getLength();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    String fileName() {
        return i_session.fileName();
    }

    private void open() throws Exception {
        boolean isNew = false;
        if (Deploy.debug) {
            if (Deploy.deleteFile) {
                System.out.println("Debug option set to DELETE file.");
                try {
                    new java.io.File(i_session.fileName()).delete();
                } catch (Exception e) {
                }
            }
        }
        try {
            if (fileName().length() > 0) {
                
                IoAdapter ioAdapter = i_config.i_ioAdapter;
                
                if(! ioAdapter.exists(fileName())){
                    isNew = true;
                    logMsg(14, fileName());
                }
                
                try {
                    boolean lockFile = Debug.lockFile && i_config.i_lockFile
                        && (!i_config.i_readonly);
                    i_file = ioAdapter.open(fileName(), lockFile, 0);
                    if (needsLockFileThread() && Debug.lockFile) {
                        i_timerFile = ioAdapter.open(fileName(), false, 0);
                    }
                } catch (DatabaseFileLockedException de) {
                    throw de;
                } catch (Exception e) {
                    Db4o.throwRuntimeException(12, fileName(), e);
                }
                if (isNew) {
                    configureNewFile();
                    if (i_config.i_reservedStorageSpace > 0) {
                        reserve(i_config.i_reservedStorageSpace);
                    }
                    write(false);
                    writeHeader(false);
                } else {
                    readThis();
                }
            } else {
                Db4o.throwRuntimeException(21);
            }
        } catch (Exception exc) {
            if (i_references != null) {
                i_references.stopTimer();
            }
            throw exc;
        }
    }

    void readBytes(byte[] bytes, int address, int length) {
        readBytes(bytes, address, 0, length);
    }

    void readBytes(byte[] bytes, int address, int addressOffset, int length) {
        
        if (DTrace.enabled) {
            DTrace.READ_BYTES.logLength(address + addressOffset, length);
        }

        try{
            i_file.blockSeek(address, addressOffset);
            i_file.read(bytes, length);
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
            
            throw new RuntimeException(ioex);
        }
    }

    void reserve(int byteCount) {
        synchronized (i_lock) {
            int address = getSlot(byteCount);
            YapWriter yb = new YapWriter(i_systemTrans, address, byteCount);
            writeBytes(yb);
            free(address, byteCount);
        }
    }

    void syncFiles() {
        try {
            i_file.sync();
            if (needsLockFileThread() && Debug.lockFile) {
                i_timerFile.sync();
            }
        } catch (Exception e) {
        }
    }

    boolean writeAccessTime() throws IOException {
        
        if (!needsLockFileThread()) {
            return true;
        }

        if (!Debug.lockFile) {
            return true;
        }

        synchronized (i_fileLock) {
            if (i_file == null) {
                return false;
            }

            long lockTime = System.currentTimeMillis();
            if (Deploy.debug) {
                YapWriter lockBytes = new YapWriter(i_systemTrans, YapConst.YAPLONG_LENGTH);
                YLong.writeLong(lockTime, lockBytes);
                i_timerFile.blockSeek(i_configBlock._address, YapConfigBlock.ACCESS_TIME_OFFSET);
                i_timerFile.write(lockBytes._buffer);
            } else {
                YLong.writeLong(lockTime, i_timerBytes);
                i_timerFile.blockSeek(i_configBlock._address, YapConfigBlock.ACCESS_TIME_OFFSET);
                i_timerFile.write(i_timerBytes);
            }
        }
        return true;
    }

    void writeBytes(YapWriter a_bytes) {
        if (i_config.i_readonly) {
            return;
        }
        if (Deploy.debug && !Deploy.flush) {
            return;
        }

        try {

            if (Deploy.debug && Deploy.overwrite) {
                if (a_bytes.getID() != YapConst.IGNORE_ID) {
                    checkXBytes(a_bytes.getAddress(), a_bytes.addressOffset(), a_bytes.getLength());
                }
            }

            if (DTrace.enabled) {
                DTrace.WRITE_BYTES.logLength(a_bytes.getAddress() + a_bytes.addressOffset(),
                    a_bytes.getLength());
            }

            i_file.blockSeek(a_bytes.getAddress(), a_bytes.addressOffset());
            i_file.write(a_bytes._buffer, a_bytes.getLength());
            if (i_backupFile != null) {
                i_backupFile.blockSeek(a_bytes.getAddress(), a_bytes.addressOffset());
                i_backupFile.write(a_bytes._buffer, a_bytes.getLength());
            }

        } catch (Exception e) {
            Db4o.throwRuntimeException(16, e);
        }
    }

    void writeXBytes(int a_address, int a_length) {
        if (Deploy.debug) {
            if (Deploy.flush) {
                if (!i_config.i_readonly) {
                    try {
                        i_file.blockSeek(a_address);
                        i_file.write(xBytes(a_address, a_length)._buffer, a_length);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}