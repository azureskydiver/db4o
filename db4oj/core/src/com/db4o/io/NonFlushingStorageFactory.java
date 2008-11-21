package com.db4o.io;

public class NonFlushingStorageFactory extends StorageFactoryDecorator {

	public NonFlushingStorageFactory(StorageFactory factory) {
		super(factory);
    }

	@Override
	protected Storage decorate(Storage storage) {
		return new NonFlushingStorage(storage);
	}
	
	private static class NonFlushingStorage extends StorageDecorator {

		public NonFlushingStorage(Storage storage) {
			super(storage);
	    }
		
		@Override
		public void sync() {
		}
	}

}
