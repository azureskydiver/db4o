package com.db4o.io;

import com.db4o.ext.*;

public class ReadOnlyBin extends BinDecorator {

	public ReadOnlyBin(Bin storage) {
	    super(storage);
    }
	
	@Override
	public void write(long position, byte[] bytes, int bytesToWrite) {
	    throw new Db4oIOException();
	}

}
