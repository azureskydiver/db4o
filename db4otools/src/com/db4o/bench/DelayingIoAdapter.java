/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.io.*;


public class DelayingIoAdapter extends VanillaIoAdapter {

	private static Delays _delays = new Delays(0,0,0,0, Delays.UNITS_MILLISECONDS);
	
	public DelayingIoAdapter(IoAdapter delegateAdapter) {
		this(delegateAdapter, _delays);
	}
	
	public DelayingIoAdapter(IoAdapter delegateAdapter, Delays delays) {
		super(delegateAdapter);
		_delays = delays;
	}
	
	public DelayingIoAdapter(IoAdapter delegateAdapter, String path, boolean lockFile, long initialLength)throws Db4oIOException {
		this(delegateAdapter, path, lockFile, initialLength, _delays);
	}
	
	public DelayingIoAdapter(IoAdapter delegateAdapter, String path, boolean lockFile, long initialLength, Delays delays)throws Db4oIOException {
		super(delegateAdapter.open(path, lockFile, initialLength, false));
		_delays = delays;
	}
	
	public IoAdapter open(String path, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
		return new DelayingIoAdapter(_delegate, path, lockFile, initialLength);
	}

	public int read(byte[] bytes, int length) throws Db4oIOException {
		delay(_delays.readDelay);
		return _delegate.read(bytes, length);
    }

    public void seek(long pos) throws Db4oIOException {
    	delay(_delays.seekDelay);
        _delegate.seek(pos);
    }

    public void sync() throws Db4oIOException {
		delay(_delays.syncDelay);
    	_delegate.sync();
    }

    public void write(byte[] buffer, int length) throws Db4oIOException {
		delay(_delays.writeDelay);
    	_delegate.write(buffer, length);
    }
	
    private void delay(long time) {
    	if (_delays.units == Delays.UNITS_MILLISECONDS) {
    		Cool.sleepIgnoringInterruption(time);
    	}
    	else if (_delays.units == Delays.UNITS_NANOSECONDS) {
    		long target = System.nanoTime() + time;
    	    while (System.nanoTime() < target) {
    	    }
    	}
    }
}
