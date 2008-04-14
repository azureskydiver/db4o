/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.ios;

import java.io.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.io.*;

public class LoggingAdapter extends IoAdapter {

	@Override
	public IoAdapter open(String path, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
		_out.println("Opening file " + path);
		return new LoggingAdapter(path, lockFile, initialLength, readOnly);
	}


	private String _path;
	private RandomAccessFile _delegate;
	private PrintStream _out = System.out;
	 
	 public LoggingAdapter(){
	 }
	 
    protected LoggingAdapter(String path, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
    	try {
    		String mode = readOnly ? "r" : "rw";
	    	_path=new File(path).getCanonicalPath();
	        _delegate = new RandomAccessFile(path, mode);
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
    	} catch (IOException e) {
    		throw new Db4oIOException(e);
    	}
    }
    
    public void setOut(PrintStream out){
    	_out = out;
    }
    
	public void close() throws Db4oIOException {
		_out.println("Closing file");
        try {
            Platform4.unlockFile(_path,_delegate);
            _delegate.close();
        } catch (IOException e) {
        	throw new Db4oIOException(e);
        } catch (Exception e) {
        }
    }


	public void delete(String path) {
		_out.println("Deleting file " + path);
		new File(path).delete();
	}

	public boolean exists(String path){
        File existingFile = new File(path);
        return  existingFile.exists() && existingFile.length() > 0;
    }

	public long getLength() throws Db4oIOException {
		long length;
		try {
		_out.println("File length:" + _delegate.length());
		length = _delegate.length();
		} catch (IOException e) {
			throw new Db4oIOException(e);
		}
		return length;
	}

	public int read(byte[] bytes, int length) throws Db4oIOException {
		int readBytes;
		try {
		_out.println("Reading " + length + " bytes");
		readBytes = _delegate.read(bytes, 0, length);
		} catch (IOException e) {
			throw new Db4oIOException(e);
		}
		return readBytes;
	}


	public void seek(long pos) throws Db4oIOException {

        if(DTrace.enabled){
            DTrace.REGULAR_SEEK.log(pos);
        }
        try {
        _out.println("Setting pointer position to  " + pos);
        _delegate.seek(pos);
        } catch (IOException e) {
        	throw new Db4oIOException(e);
        }
	}


	public void sync() throws Db4oIOException {
		_out.println("Synchronizing");
		try {
		_delegate.getFD().sync();
		} catch (IOException e) {
			throw new Db4oIOException(e);
		}
	}

	
	public void write(byte[] buffer, int length) throws Db4oIOException {
		_out.println("Writing " + length + " bytes");
		try {
        _delegate.write(buffer, 0, length);
		} catch (IOException e) {
			new Db4oIOException(e);
		}
		
	}

}
