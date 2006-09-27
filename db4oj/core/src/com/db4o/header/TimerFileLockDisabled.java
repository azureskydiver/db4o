/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.header;


/**
 * @exclude
 */
public class TimerFileLockDisabled  extends TimerFileLock{
    
    public void checkHeaderLock() {
    }

    public void checkOpenTime() {
    }

    public boolean lockFile() {
        return false;
    }

    public long openTime() {
        return 0;
    }

    public void run() {
    }

    public void setOpenTimeAddress(int address, int offset) {
    }

    public void start() {
    }

    public void writeHeaderLock(){
    }

    public void writeOpenTime() {
    }
    
}
