/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.*;

import com.db4o.ext.*;

class YapRandomAccessFile extends YapFile {

    private Session                   i_session;

    private RandomAccessFile          i_file;
    private RandomAccessFile          i_timerFile;
    private volatile RandomAccessFile i_backupFile;
    private byte[]                    i_timerBytes = new byte[YapConst.LONG_BYTES];
    private byte[]                    i_seekBytes;
    private Object                    i_fileLock;

    YapRandomAccessFile(Session a_session) throws Exception {
        super(null);
        synchronized (i_lock) {
            i_fileLock = new Object();
            if (Tuning.symbianSeek) {
                i_seekBytes = new byte[500];
            }
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
                i_backupFile = new RandomAccessFile(path, "rw");
                i_backupFile.seek(i_file.length() - 1);
                i_backupFile.write(new byte[] { 0});
            } catch (Exception e) {
                i_backupFile = null;
                Db4o.throwRuntimeException(12, path);
            }
        }
        int pos = 0;
        int bufferlength = 8000;
        byte[] buffer = new byte[bufferlength];
        do {
            synchronized (i_lock) {
                i_file.seek(pos);
                int read = i_file.read(buffer, 0, bufferlength);
                i_backupFile.seek(pos);
                i_backupFile.write(buffer, 0, read);
                pos += read;
                i_lock.notify();
            }
        } while (pos < i_file.length());
        synchronized (i_lock) {
            i_backupFile.close();
            i_backupFile = null;
        }
    }

    boolean close2() {
        boolean stopSession = true;
        synchronized (Db4o.lock) {
            stopSession = i_session.closeInstance();
            if (stopSession) {
                freePrefetchedPointers();
                if (Deploy.debug) {
                    i_entryCounter++;
                    write(true);
                } else {
                    try {
                        i_entryCounter++;
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
                        try {
                            Platform.unlock(i_file);
                        } catch (Exception e) {
                        }
                        i_file.close();
                        i_file = null;
                        if (hasLockFileThread() && Debug.lockFile) {
                            YapWriter lockBytes = new YapWriter(i_systemTrans,
                                YapConst.YAPLONG_LENGTH);
                            YLong.writeLong(0, lockBytes);
                            i_timerFile.seek(i_timerAddress);
                            i_timerFile.write(lockBytes.i_bytes);
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

    void copy(int a_oldAddress, int a_newAddress, int a_length) {

        // TODO: Check for XBytes in debug mode.

        try {
            if (Tuning.symbianSeek) {
                symbianSeek(a_newAddress);
            }
            byte[] copyBytes = new byte[a_length];
            i_file.seek(a_oldAddress);
            i_file.read(copyBytes);
            i_file.seek(a_newAddress);
            i_file.write(copyBytes);
        } catch (Exception e) {
            Db4o.throwRuntimeException(16, e);
        }
        //        if(Deploy.debug){
        //            writeXBytes(a_oldAddress, a_length);
        //        }
    }

    void emergencyClose() {
        super.emergencyClose();
        try {
            Platform.unlock(i_file);
        } catch (Exception e) {
        }
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

    int fileLength() {
        try {
            return (int) i_file.length();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    String fileName() {
        return i_session.fileName();
    }

    private void open() throws Exception{
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
        try{
	        if (fileName().length() > 0) {
	            File existingFile = new File(fileName());
	            if (!existingFile.exists() || existingFile.length() == 0) {
	                isNew = true;
	                logMsg(14, fileName());
	            }
	            try {
	                i_file = new RandomAccessFile(fileName(), "rw");
	                if (Debug.lockFile) {
	                    if (i_config.i_lockFile && (!i_config.i_readonly)) {
	                        Platform.lock(i_file);
	                    }
	                }
	                if (hasLockFileThread() && Debug.lockFile) {
	                    i_timerFile = new RandomAccessFile(fileName(), "rw");
	                }
	            } catch (DatabaseFileLockedException de) {
	                throw de;
	            } catch (Exception e) {
	                Db4o.throwRuntimeException(12, fileName(), e);
	            }
	            if (isNew) {
	                if (i_config.i_reservedStorageSpace > 0) {
	                    reserve(i_config.i_reservedStorageSpace);
	                }
	                configureNewFile();
	                write(false);
	                writeHeader(false);
	            } else {
	                readThis();
	            }
	        } else {
	            Db4o.throwRuntimeException(21);
	        }
        }catch(Exception exc){
            if(i_references != null){
                i_references.stopTimer();
            }
            throw exc;
        }
    }

    void readBytes(byte[] a_bytes, int a_address, int a_length) {
        try {
            i_file.seek(a_address);
            i_file.read(a_bytes, 0, a_length);
        } catch (Exception e) {
            if (Debug.atHome) {
                e.printStackTrace();
            }
            Db4o.throwRuntimeException(13, e);
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
        if (!Deploy.csharp) {
            try {
                i_file.getFD().sync();
                if (hasLockFileThread() && Debug.lockFile) {
                    i_timerFile.getFD().sync();
                }
            } catch (Exception e) {
            }
        }
    }

    boolean writeAccessTime() throws IOException {
        if (hasLockFileThread() && Debug.lockFile) {
            synchronized (i_fileLock) {
                if (i_file == null) {
                    return false;
                }
                long lockTime = System.currentTimeMillis();
                if (Deploy.debug) {
                    YapWriter lockBytes = new YapWriter(i_systemTrans,
                        YapConst.YAPLONG_LENGTH);
                    YLong.writeLong(lockTime, lockBytes);
                    i_timerFile.seek(i_timerAddress);
                    i_timerFile.write(lockBytes.i_bytes);
                } else {
                    YLong.writeLong(lockTime, i_timerBytes);
                    i_timerFile.seek(i_timerAddress);
                    i_timerFile.write(i_timerBytes);
                }
            }
        }
        return true;
    }

    void writeBytes(YapWriter a_bytes) {
        if (!i_config.i_readonly) {
            if (Deploy.debug && !Deploy.flush) {
                return;
            } else {
                try {
                    if (Deploy.debug && Deploy.overwrite) {
                        if (a_bytes.getID() != YapConst.IGNORE_ID) {
                            try {
                                byte[] checkXBytes = new byte[a_bytes
                                    .getLength()];
                                i_file.seek(a_bytes.getAddress());
                                i_file.read(checkXBytes);
                                for (int i = 0; i < checkXBytes.length; i++) {
                                    if (checkXBytes[i] != YapConst.XBYTE) {
                                        String msg = "XByte corruption adress:"
                                            + a_bytes.getAddress() + " length:"
                                            + a_bytes.getLength();
                                        throw new RuntimeException(msg);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (Tuning.symbianSeek) {
                        symbianSeek(a_bytes.getAddress());
                    }
                    i_file.seek(a_bytes.getAddress());
                    i_file.write(a_bytes.i_bytes, 0, a_bytes.getLength());
                    if (i_backupFile != null) {
                        i_backupFile.seek(a_bytes.getAddress());
                        i_backupFile.write(a_bytes.i_bytes, 0, a_bytes
                            .getLength());
                    }
                } catch (Exception e) {
                    Db4o.throwRuntimeException(16, e);
                }
            }
        }
    }

    private void symbianSeek(int a_address) throws IOException {
        if (Tuning.symbianSeek) {
            if (a_address > i_file.length()) {
                int len = a_address - (int) i_file.length();
                i_file.seek(i_file.length());
                if (len < 500) {
                    i_file.write(i_seekBytes, 0, len);
                } else {
                    i_file.write(new byte[len]);
                }
            }
        }
    }

    void writeXBytes(int a_address, int a_length) {
        if (Deploy.debug) {
            if (Deploy.flush) {
                if (!i_config.i_readonly) {
                    try {
                        i_file.seek(a_address);
                        i_file.write(xBytes(a_address, a_length).i_bytes, 0,
                            a_length);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}