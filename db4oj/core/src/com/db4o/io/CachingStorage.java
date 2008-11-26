package com.db4o.io;

import com.db4o.ext.*;
import com.db4o.internal.caching.*;

public class CachingStorage extends StorageDecorator {

	private static int DEFAULT_PAGE_COUNT = 64;
	private static int DEFAULT_PAGE_SIZE = 1024;
	
	private int _pageCount;
	private int _pageSize;

	public CachingStorage(Storage storage) {
	    this(storage, DEFAULT_PAGE_COUNT, DEFAULT_PAGE_SIZE);
    }

	public CachingStorage(Storage storage, int pageCount, int pageSize) {
	    super(storage);
		_pageCount = pageCount;
		_pageSize = pageSize;
    }

	@Override
	public Bin open(String uri, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
	    final Bin storage = super.open(uri, lockFile, initialLength, readOnly);
	    if (readOnly) {
	    	return new ReadOnlyBin(new NonFlushingCachingBin(storage, newCache(), _pageCount, _pageSize));
	    }
	    return new CachingBin(storage, newCache(), _pageCount, _pageSize);
	}

	protected Cache4<Object, Object> newCache() {
	    return CacheFactory.new2QCache(_pageCount);
    }

	private static final class NonFlushingCachingBin extends CachingBin {
		
		public NonFlushingCachingBin(Bin bin, Cache4 cache, int pageCount, int pageSize) throws Db4oIOException {
			super(bin, cache, pageCount, pageSize);
		}
		
		@Override 
		protected void flushAllPages() {
		}
	}
}
