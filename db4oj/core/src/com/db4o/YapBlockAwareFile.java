/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.*;

class YapBlockAwareFile {

    int                            _blockSize = 1;

    private final RandomAccessFile _delegate;

    private final byte[]           _seekBytes;

    public YapBlockAwareFile(String path) throws FileNotFoundException {
        _delegate = new RandomAccessFile(path, "rw");
        if (Tuning.symbianSeek) {
            _seekBytes = new byte[500];
        } else {
            _seekBytes = null;
        }
    }

    public YapBlockAwareFile(String path, long initialLength) throws IOException {
        this(path);
        _delegate.seek(initialLength - 1);
        _delegate.write(new byte[] {0});
    }

    public void blockSeek(int address) throws IOException {
        regularSeek((long) address * (long) _blockSize);
    }

    public void blockSeek(int address, int newAddressOffset) throws IOException {
        regularSeek((long) address * (long) _blockSize + (long) newAddressOffset);
    }

    public void blockSize(int blockSize) {
        _blockSize = blockSize;
    }

    public void close() throws IOException {
        _delegate.close();
    }

    // TODO: the method name should be "length" after the
    // converter does not redirect all length calls to JavaSystem
    public long getLength() throws IOException {
        return _delegate.length();
    }

    public void lock() {
        Platform.lock(_delegate);
    }

    public int read(byte[] buffer) throws IOException {
        return _delegate.read(buffer, 0, buffer.length);
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

    public void write(byte[] bytes) throws IOException {
        _delegate.write(bytes);
    }

    public void write(byte[] buffer, int length) throws IOException {
        _delegate.write(buffer, 0, length);
    }

}
