package com.db4o.io;

public class NonFlushingStorageFactory extends StorageFactoryDecorator {

	public NonFlushingStorageFactory(StorageFactory factory) {
		super(factory);
    }

	@Override
	protected Bin decorate(Bin storage) {
		return new NonFlushingStorage(storage);
	}
	
	private static class NonFlushingStorage extends StorageDecorator {

		public NonFlushingStorage(Bin storage) {
			super(storage);
	    }
		
		@Override
		public void sync() {
		}
	}

}
