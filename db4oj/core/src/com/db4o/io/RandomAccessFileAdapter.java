/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.io;

import java.io.*;

import com.db4o.*;

public class RandomAccessFileAdapter extends IoAdapter {

    private RandomAccessFile _delegate;

    private byte[]           _seekBytes;
    
    public RandomAccessFileAdapter(){
    }

    private RandomAccessFileAdapter(String path, boolean lockFile, long initialLength) throws IOException {
        _delegate = new RandomAccessFile(path, "rw");
        if (Tuning.symbianSeek) {
            _seekBytes = new byte[500];
        } else {
            _seekBytes = null;
        }
        if(initialLength>0) {
	        _delegate.seek(initialLength - 1);
	        _delegate.write(new byte[] {0});
        }
        if(lockFile){
            Platform4.lock(_delegate);
        }
    }

    public void close() throws IOException {
        try {
            Platform4.unlock(_delegate);
        } catch (Exception e) {
        }
        _delegate.close();
    }

    public long getLength() throws IOException {
        return _delegate.length();
    }

    public IoAdapter open(String path, boolean lockFile, long initialLength) throws IOException {
        return new RandomAccessFileAdapter(path, lockFile, initialLength);
    }

    public int read(byte[] bytes, int length) throws IOException {
        return _delegate.read(bytes, 0, length);
    }

    public void seek(long pos) throws IOException {

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
                    seek(pos);
                    return;
                }
            }
        }

        _delegate.seek(pos);

    }

    public void sync() throws IOException {
        _delegate.getFD().sync();
    }

    public void write(byte[] buffer, int length) throws IOException {
        _delegate.write(buffer, 0, length);
    }
}
