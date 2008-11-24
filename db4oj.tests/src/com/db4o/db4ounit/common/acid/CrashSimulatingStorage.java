/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.acid;

import com.db4o.ext.*;
import com.db4o.io.*;


public class CrashSimulatingStorage extends StorageDecorator {
    
    CrashSimulatingBatch _batch;
        
    public CrashSimulatingStorage(Storage storage) {
        super(storage);
        _batch = new CrashSimulatingBatch();
    }
    
    @Override
    protected Bin decorate(Bin bin) {
    	return new CrashSimulatingBin(bin, _batch);
    }

    static class CrashSimulatingBin extends BinDecorator {
    	
    	private CrashSimulatingBatch _batch;
        long _curPos;
  	
	    public CrashSimulatingBin(Bin bin, CrashSimulatingBatch batch) {
			super(bin);
			_batch = batch;
		}

		public int read(long pos, byte[] bytes, int length) throws Db4oIOException {
	        _curPos=pos;
	        int readBytes = super.read(pos, bytes, length);
	        if(readBytes > 0){
	            _curPos += readBytes;
	        }
	        return readBytes;
	    }
	
	    public void write(long pos, byte[] buffer, int length) throws Db4oIOException {
	        _curPos=pos;
	        super.write(pos, buffer, length);
	        byte[] copy=new byte[buffer.length];
	        System.arraycopy(buffer, 0, copy, 0, length);
	        _batch.add(copy, _curPos, length);
	        _curPos+= length;
	    }
	    
	    public void sync() throws Db4oIOException {
	        super.sync();
	        _batch.sync();
	    }
    
    }
}
