package com.db4o.io;

public class NonFlushingStorage extends StorageDecorator {

	public NonFlushingStorage(Storage storage) {
		super(storage);
    }

	@Override
	protected Bin decorate(Bin storage) {
		return new NonFlushingBin(storage);
	}
	
	private static class NonFlushingBin extends BinDecorator {

		public NonFlushingBin(Bin storage) {
			super(storage);
	    }
		
		@Override
		public void sync() {
		}
	}

}
