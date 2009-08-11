/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.db4ounit.optional.monitoring;

import com.db4o.io.*;

@decaf.Remove
class SyncCountingStorage extends StorageDecorator {
	
	int _numberOfSyncCalls;

	public SyncCountingStorage(Storage storage) {
		super(storage);
	}
	
	public int numberOfSyncCalls() {
		return _numberOfSyncCalls;
	}
	
	@Override
	protected Bin decorate(BinConfiguration config, Bin bin) {
		return new BinDecorator(bin) {
			@Override
			public void sync() {
				++_numberOfSyncCalls;
				super.sync();
			}
		};
	}
	
}