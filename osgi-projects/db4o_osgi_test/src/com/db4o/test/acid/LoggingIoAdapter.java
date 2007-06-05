/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
package com.db4o.test.acid;

import java.io.*;

import com.db4o.*;
import com.db4o.io.*;

public class LoggingIoAdapter extends VanillaIoAdapter {
	private final static int READ=1;
	private final static int WRITE=2;
	private final static int SYNC=4;
	
	private PrintStream _out;
	private int _config;
	private long _curpos;
	
    public LoggingIoAdapter(IoAdapter delegateAdapter,PrintStream out) {
    	this(delegateAdapter,out,WRITE);
    }

    public LoggingIoAdapter(IoAdapter delegateAdapter,PrintStream out,int config) {
        super(delegateAdapter);
        _out=out;
        _config=config;
    }

    private LoggingIoAdapter(IoAdapter delegateAdapter, String path, boolean lockFile, long initialLength, PrintStream out,int config) throws Db4oIOException {
        super(delegateAdapter.open(path, lockFile, initialLength));
        _out=out;
        _config=config;
        _curpos=0;
    }

	public IoAdapter open(String path, boolean lockFile, long initialLength) throws Db4oIOException {
		return new LoggingIoAdapter(_delegate,path,lockFile,initialLength,_out,_config);
	}

    public int read(byte[] bytes, int length) throws Db4oIOException {
    	if(config(READ)) {
    		_out.println("READ "+_curpos+","+length);
    	}
        return _delegate.read(bytes, length);
    }

    public void seek(long pos) throws Db4oIOException {
    	_curpos=pos;
        _delegate.seek(pos);
    }

    public void sync() throws Db4oIOException {
    	if(config(SYNC)) {
    		_out.println("SYNC");
    	}
        _delegate.sync();
    }

    public void write(byte[] buffer, int length) throws Db4oIOException {
    	if(config(WRITE)) {
    		_out.println("WRITE "+_curpos+","+length);
    	}
        _delegate.write(buffer, length);
    }

    private boolean config(int mask) {
    	return (_config&mask)!=0;
    }
}
