/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.acid;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.internal.transactionlog.*;

public class CrashSimulatingWrite {
    
    byte[] _data;
    long _offset;
    int _length;
    
    byte[] _lockFileData;
    byte[] _logFileData;
    
    public CrashSimulatingWrite(byte[] data, long offset, int length, byte[] lockFileData, byte[] logFileData) {
        _data = data;
        _offset = offset;
        _length = length;
        _lockFileData = lockFileData;
        _logFileData = logFileData;
    }

    public void write(String path, RandomAccessFile raf) throws IOException {
        raf.seek(_offset);
        raf.write(_data, 0, _length);
        write(FileBasedTransactionLogHandler.lockFileName(path), _lockFileData);
        write(FileBasedTransactionLogHandler.logFileName(path), _logFileData);
    }
    
    public String toString(){
        return "A " + _offset + " L " + _length;
    }
    
    private void write(String fileName, byte[] bytes){
    	if(bytes == null){
    		return;
    	}
    	try {
        	RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        	raf.write(bytes);
			raf.close();
		} catch (IOException e) {
			throw new Db4oException(e);
		}
    }

}
