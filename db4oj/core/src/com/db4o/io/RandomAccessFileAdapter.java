/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.db4o.DTrace;
import com.db4o.ext.DatabaseFileLockedException;
import com.db4o.internal.Platform4;

/**
 * IO adapter for random access files.
 */
public class RandomAccessFileAdapter extends IoAdapter {

	private String _path;
    private RandomAccessFile _delegate;

    public RandomAccessFileAdapter(){
    }

    protected RandomAccessFileAdapter(String path, boolean lockFile, long initialLength) throws IOException {
    	_path=new File(path).getCanonicalPath();
        _delegate = new RandomAccessFile(_path, "rw");
        if(initialLength>0) {
	        _delegate.seek(initialLength - 1);
	        _delegate.write(new byte[] {0});
        }
        if(lockFile){
        	try {
				Platform4.lockFile(_path, _delegate);
			} catch (DatabaseFileLockedException e) {
				_delegate.close();
				throw e;
			}
        }
    }

    public void close() throws IOException {
        try {
            Platform4.unlockFile(_path,_delegate);
        } catch (Exception e) {
        }
        _delegate.close();
    }
    
	public void delete(String path) {
		new File(path).delete();
	}

    public boolean exists(String path){
        File existingFile = new File(path);
        return  existingFile.exists() && existingFile.length() > 0;
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
        _delegate.seek(pos);

    }

    public void sync() throws IOException {
        _delegate.getFD().sync();
    }

    public void write(byte[] buffer, int length) throws IOException {
        _delegate.write(buffer, 0, length);
    }
}
