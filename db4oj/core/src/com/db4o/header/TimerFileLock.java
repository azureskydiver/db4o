/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.header;

import java.io.*;

import com.db4o.*;


/**
 * @exclude
 */
public abstract class TimerFileLock implements Runnable{
    
    public static TimerFileLock forFile(YapFile file){
        if(lockFile(file)){
            return new TimerFileLockEnabled(file);
        }
        return new TimerFileLockDisabled();
    }
    
    private static boolean lockFile(YapFile file){
        if(! Debug.lockFile){
            return false;
        }
        return file.needsLockFileThread();
    }

    public abstract void checkHeaderLock();

    public abstract void checkOpenTime();

    public abstract boolean lockFile();

    public abstract long openTime();

    public abstract void setAddresses(int baseAddress, int openTimeOffset, int accessTimeOffset);

    public abstract void start() throws IOException;

    public abstract void writeHeaderLock();

    public abstract void writeOpenTime();

    public abstract void close() throws IOException;

}
