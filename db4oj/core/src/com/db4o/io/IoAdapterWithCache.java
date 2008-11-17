/* Copyright (C) 2006 - 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.io;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.caching.*;

/**
 */
public abstract class IoAdapterWithCache extends IoAdapter {

	private long _position;

	private int _pageSize;

	private int _pageCount;

	private long _fileLength;

	private IoAdapter _io;

	private boolean _readOnly;

	private Cache4<Long, Page> _cache;

	private ObjectPool<Page> _pagePool;
	
	private static int DEFAULT_PAGE_SIZE = 1024;

	private static int DEFAULT_PAGE_COUNT = 64;

	private Procedure4<Page> _onDiscardPage = new Procedure4<Page>() {
    	public void apply(Page discardedPage) {
    		flushPage(discardedPage);
    		_pagePool.returnObject(discardedPage);
        }
    };

	/**
	 * Creates an instance of CachedIoAdapter with the default page size and
	 * page count.
	 * 
	 * @param ioAdapter
	 *            delegate IO adapter (RandomAccessFileAdapter by default)
	 */
	public IoAdapterWithCache(IoAdapter ioAdapter) {
		this(ioAdapter, DEFAULT_PAGE_SIZE, DEFAULT_PAGE_COUNT);
	}

	/**
	 * Creates an instance of CachedIoAdapter with a custom page size and page
	 * count.<br>
	 * 
	 * @param ioAdapter
	 *            delegate IO adapter (RandomAccessFileAdapter by default)
	 * @param pageSize
	 *            cache page size
	 * @param pageCount
	 *            allocated amount of pages
	 */
	public IoAdapterWithCache(IoAdapter ioAdapter, int pageSize, int pageCount) {
		_io = ioAdapter;
		_pageSize = pageSize;
		_pageCount = pageCount;
	}

	/**
	 * Creates an instance of CachedIoAdapter with extended parameters.<br>
	 * 
	 * @param path
	 *            database file path
	 * @param lockFile
	 *            determines if the file should be locked
	 * @param initialLength
	 *            initial file length, new writes will start from this point
	 * @param readOnly
	 *            if the file should be used in read-onlyt mode.
	 * @param io
	 *            delegate IO adapter (RandomAccessFileAdapter by default)
	 * @param pageSize
	 *            cache page size
	 * @param pageCount
	 *            allocated amount of pages
	 */
	private IoAdapterWithCache(String path, boolean lockFile, long initialLength, boolean readOnly, IoAdapter io,
	        Cache4<Long, Page> cache, int pageCount, int pageSize) throws Db4oIOException {
		_readOnly = readOnly;
		_pageSize = pageSize;

		_io = io.open(path, lockFile, initialLength, readOnly);

		_pagePool = new SimpleObjectPool<Page>(newPagePool(pageCount));
		_cache = cache;
		_position = initialLength;
		_fileLength = _io.getLength();
	}

	private Page[] newPagePool(int pageCount) {
	    final Page[] pages = new Page[pageCount];
		for (int i=0; i<pages.length; ++i) {
			pages[i] = new Page(_pageSize);
		}
	    return pages;
    }

	/**
	 * Creates and returns a new CachedIoAdapter <br>
	 * 
	 * @param path
	 *            database file path
	 * @param lockFile
	 *            determines if the file should be locked
	 * @param initialLength
	 *            initial file length, new writes will start from this point
	 */
	public IoAdapter open(String path, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
		return new IoAdapterWithCache(path, lockFile, initialLength, readOnly, _io, newCache(_pageCount), _pageCount, _pageSize) {
			@Override
			protected Cache4 newCache(int pageCount) {
				throw new IllegalStateException();
			}
		};
	}

	/**
	 * Deletes the database file
	 * 
	 * @param path
	 *            file path
	 */
	public void delete(String path) {
		_io.delete(path);
	}

	/**
	 * Checks if the file exists
	 * 
	 * @param path
	 *            file path
	 */
	public boolean exists(String path) {
		return _io.exists(path);
	}

	/**
	 * Reads the file into the buffer using pages from cache. If the next page
	 * is not cached it will be read from the file.
	 * 
	 * @param buffer
	 *            destination buffer
	 * @param length
	 *            how many bytes to read
	 */
	public int read(byte[] buffer, int length) throws Db4oIOException {
		long startAddress = _position;
		int bytesToRead = length;
		int totalRead = 0;
		while (bytesToRead > 0) {
			final Page page = getPage(startAddress, _producerFromDisk);
			final int readBytes = page.read(buffer, totalRead, startAddress, bytesToRead);
			if (readBytes <= 0) {
				break;
			}
			bytesToRead -= readBytes;
			startAddress += readBytes;
			totalRead += readBytes;
		}
		_position = startAddress;
		return totalRead == 0 ? -1 : totalRead;
	}

	/**
	 * Writes the buffer to cache using pages
	 * 
	 * @param buffer
	 *            source buffer
	 * @param length
	 *            how many bytes to write
	 */
	public void write(byte[] buffer, int length) throws Db4oIOException {
		validateReadOnly();
		long startAddress = _position;
		int bytesToWrite = length;
		int bufferOffset = 0;
		while (bytesToWrite > 0) {
			// page doesn't need to loadFromDisk if the whole page is dirty
			boolean loadFromDisk = (bytesToWrite < _pageSize) || (startAddress % _pageSize != 0);

			final Page page = getPage(startAddress, loadFromDisk);

			final int writtenBytes = page.write(buffer, bufferOffset, startAddress, bytesToWrite);

			bytesToWrite -= writtenBytes;
			startAddress += writtenBytes;
			bufferOffset += writtenBytes;
		}
		long endAddress = startAddress;
		_position = endAddress;
		_fileLength = Math.max(endAddress, _fileLength);
	}

	private void validateReadOnly() {
		if (_readOnly) {
			throw new Db4oIOException();
		}
	}

	/**
	 * Flushes cache to a physical storage
	 */
	public void sync() throws Db4oIOException {
		validateReadOnly();
		flushAllPages();
		_io.sync();
	}

	/**
	 * Returns the file length
	 */
	public long getLength() throws Db4oIOException {
		return _fileLength;
	}

	/**
	 * Flushes and closes the file
	 */
	public void close() throws Db4oIOException {
		try {
			flushAllPages();
		} finally {
			_io.close();
		}
	}

	public IoAdapter delegatedIoAdapter() {
		return _io.delegatedIoAdapter();
	}

	final Function4<Long, Page> _producerFromDisk = new Function4<Long, Page>() {
		public Page apply(Long pageAddress) {
			// in case that page is not found in the cache
			final Page newPage = _pagePool.borrowObject();
			loadPage(newPage, pageAddress.longValue());
			return newPage;
		}
	};
	
	final Function4<Long, Page> _producerFromCache = new Function4<Long, Page>() {
		public Page apply(Long pageAddress) {
			// in case that page is not found in the cache
			final Page newPage = _pagePool.borrowObject();
			resetPageAddress(newPage, pageAddress.longValue());
			return newPage;
		}
	};
	
	private Page getPage(final long startAddress, final boolean loadFromDisk) throws Db4oIOException {
		final Function4<Long, Page> producer = loadFromDisk ? _producerFromDisk : _producerFromCache;
		return getPage(startAddress, producer);
	}

	private Page getPage(final long startAddress, final Function4<Long, Page> producer) {
	    final Page page = _cache.produce(pageAddressFor(startAddress), producer, _onDiscardPage);
		page.ensureEndAddress(_fileLength);
		return page;
    }

	private Long pageAddressFor(long startAddress) {
		return (startAddress / _pageSize) * _pageSize;
    }

	private void resetPageAddress(Page page, long startAddress) {
		page.startAddress(startAddress);
		page.endAddress(startAddress + _pageSize);
	}

	private void flushAllPages() throws Db4oIOException {
		 for (Page p : _cache) {
			 flushPage(p);
		 }
	}

	private void flushPage(Page page) throws Db4oIOException {
		if (!page._dirty) {
			return;
		}
		ioSeek(page.startAddress());
		writePageToDisk(page);
	}

	private void loadPage(Page page, long pos) throws Db4oIOException {
		long startAddress = pos - pos % _pageSize;
		page.startAddress(startAddress);
		ioSeek(page._startAddress);
		int count = ioRead(page);
		if (count > 0) {
			page.endAddress(startAddress + count);
		} else {
			page.endAddress(startAddress);
		}
	}

	private int ioRead(Page page) throws Db4oIOException {
		return _io.read(page._buffer);
	}

	private void writePageToDisk(Page page) throws Db4oIOException {
		try {
			_io.write(page._buffer, page.size());
			page._dirty = false;
		} catch (Db4oIOException e) {
			_readOnly = true;
			throw e;
		}
	}

	/**
	 * Moves the pointer to the specified file position
	 * 
	 * @param pos
	 *            position within the file
	 */
	public void seek(long pos) throws Db4oIOException {
		_position = pos;
	}

	private void ioSeek(long pos) throws Db4oIOException {
		_io.seek(pos);
	}

	private static class Page {

		byte[] _buffer;

		long _startAddress = -1;

		long _endAddress;

		private final int _bufferSize;

		boolean _dirty;

		private byte[] zeroBytes;

		public Page(int size) {
			_bufferSize = size;
			_buffer = new byte[_bufferSize];
		}

		/*
		 * This method must be invoked before page.write/read, because seek and
		 * write may write ahead the end of file.
		 */
		void ensureEndAddress(long fileLength) {
			long bufferEndAddress = _startAddress + _bufferSize;
			if (_endAddress < bufferEndAddress && fileLength > _endAddress) {
				long newEndAddress = Math.min(fileLength, bufferEndAddress);
				if (zeroBytes == null) {
					zeroBytes = new byte[_bufferSize];
				}
				System.arraycopy(zeroBytes, 0, _buffer, (int) (_endAddress - _startAddress),
				        (int) (newEndAddress - _endAddress));
				_endAddress = newEndAddress;
			}
		}

		long endAddress() {
			return _endAddress;
		}

		void startAddress(long address) {
			_startAddress = address;
		}

		long startAddress() {
			return _startAddress;
		}

		void endAddress(long address) {
			_endAddress = address;
		}

		int size() {
			return (int) (_endAddress - _startAddress);
		}

		int read(byte[] out, int outOffset, long startAddress, int length) {
			int bufferOffset = (int) (startAddress - _startAddress);
			int pageAvailbeDataSize = (int) (_endAddress - startAddress);
			int readBytes = Math.min(pageAvailbeDataSize, length);
			if (readBytes <= 0) { // meaning reach EOF
				return -1;
			}
			System.arraycopy(_buffer, bufferOffset, out, outOffset, readBytes);
			return readBytes;
		}

		int write(byte[] data, int dataOffset, long startAddress, int length) {
			int bufferOffset = (int) (startAddress - _startAddress);
			int pageAvailabeBufferSize = _bufferSize - bufferOffset;
			int writtenBytes = Math.min(pageAvailabeBufferSize, length);
			System.arraycopy(data, dataOffset, _buffer, bufferOffset, writtenBytes);
			long endAddress = startAddress + writtenBytes;
			if (endAddress > _endAddress) {
				_endAddress = endAddress;
			}
			_dirty = true;
			return writtenBytes;
		}
	}

	protected abstract Cache4<Long, Page> newCache(int pageCount);

}
