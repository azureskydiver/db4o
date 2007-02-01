/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.ios;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;

import com.db4o.DTrace;
import com.db4o.inside.*;
import com.db4o.io.IoAdapter;

public class LoggingAdapter extends IoAdapter {

	private RandomAccessFile _delegate;
	private PrintStream _out = System.out;
	 
	 public LoggingAdapter(){
	 }
	 
    protected LoggingAdapter(String path, boolean lockFile, long initialLength) throws IOException {
        _delegate = new RandomAccessFile(path, "rw");
        if(initialLength>0) {
	        _delegate.seek(initialLength - 1);
	        _delegate.write(new byte[] {0});
        }
        if(lockFile){
            Platform4.lockFile(_delegate);
        }
    }
    
    public void setOut(PrintStream out){
    	_out = out;
    }
    
	public void close() throws IOException {
		_out.println("Closing file");
        try {
            Platform4.unlockFile(_delegate);
        } catch (Exception e) {
        }
        _delegate.close();
    }


	public void delete(String path) {
		_out.println("Deleting file " + path);
		new File(path).delete();
	}

	public boolean exists(String path){
        File existingFile = new File(path);
        return  existingFile.exists() && existingFile.length() > 0;
    }

	public long getLength() throws IOException {
		_out.println("File length:" + _delegate.length());
		return _delegate.length();
	}

	public IoAdapter open(String path, boolean lockFile, long initialLength) throws IOException {
		_out.println("Opening file " + path);
        return new LoggingAdapter(path, lockFile, initialLength);
    }


	public int read(byte[] bytes, int length) throws IOException {
		_out.println("Reading " + length + " bytes");
        return _delegate.read(bytes, 0, length);
	}


	public void seek(long pos) throws IOException {

        if(DTrace.enabled){
            DTrace.REGULAR_SEEK.log(pos);
        }
        _out.println("Setting pointer position to  " + pos);
        _delegate.seek(pos);
	}


	public void sync() throws IOException {
		_out.println("Synchronizing");
		_delegate.getFD().sync();
	}

	
	public void write(byte[] buffer, int length) throws IOException {
		_out.println("Writing " + length + " bytes");
        _delegate.write(buffer, 0, length);
		
	}

}
