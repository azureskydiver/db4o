/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.io.jdkdefault;

import java.io.*;

import com.db4o.DTrace;
import com.db4o.Platform;
import com.db4o.Tuning;
import com.db4o.io.ObjectFile;

public class RandomAccessObjectFile extends ObjectFile {

    private int                            _blockSize = 1;

    private final RandomAccessFile _delegate;

    private final byte[]           _seekBytes;

    public RandomAccessObjectFile(String path) throws FileNotFoundException {
        _delegate = new RandomAccessFile(path, "rw");
        if (Tuning.symbianSeek) {
            _seekBytes = new byte[500];
        } else {
            _seekBytes = null;
        }
    }

    public RandomAccessObjectFile(String path, long initialLength) throws IOException {
        this(path);
        if(initialLength>0) {
	        _delegate.seek(initialLength - 1);
	        _delegate.write(new byte[] {0});
        }
    }

    public void close() throws IOException {
        _delegate.close();
    }

    public long length() throws IOException {
        return _delegate.length();
    }

    public void lock() {
        Platform.lock(_delegate);
    }

    public int read(byte[] bytes, int length) throws IOException {
        return _delegate.read(bytes, 0, length);
    }

    public void regularSeek(long pos) throws IOException {

        if(DTrace.enabled){
            DTrace.REGULAR_SEEK.log(pos);
        }

        /** 
         * Workaround for the Symbian JDK that does not allow you to seek
         * beyond the length of the file.
         */
        if (Tuning.symbianSeek) {
            if (pos > _delegate.length()) {
                int len = (int) (pos - _delegate.length());
                _delegate.seek(_delegate.length());
                if (len < 500) {
                    _delegate.write(_seekBytes, 0, len);
                } else {
                    _delegate.write(new byte[len]);
                    regularSeek(pos);
                    return;
                }
            }
        }

        _delegate.seek(pos);

    }

    public void sync() throws IOException {
        
        // FIXME: fix on .NET and enable here 
        
        // _delegate.getFD().sync();
    }

    public void unlock() {
        Platform.unlock(_delegate);
    }

    public void write(byte[] buffer, int length) throws IOException {
        _delegate.write(buffer, 0, length);
    }
}
