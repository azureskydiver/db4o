/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.io;

import java.io.IOException;

/**
 * CachedIoAdapter is an IOAdapter for random access files, which caches data for IO access.
 * Its functionality is similar to OS cache.<br>
 * Example:<br>
 * <code>delegateAdapter = new RandomAccessFileAdapter();</code><br>
 * <code>Db4o.configure().io(new CachedIoAdapter(delegateAdapter));</code><br>
 */
public class CachedIoAdapter extends IoAdapter {

	private Page _head;

	private Page _tail;

	private long _position;

	private int _pageSize;

	private int _pageCount;

	private long _fileLength;

	private long _filePointer;

	private IoAdapter _io;
	
	private static int DEFAULT_PAGE_SIZE = 1024;
	
	private static int DEFAULT_PAGE_COUNT = 64;

	// private Hashtable4 _posPageMap = new Hashtable4(PAGE_COUNT);

	/**
	 * Creates an instance of CachedIoAdapter with the default 
	 * page size and page count.  
	 * @param IoAdapter
	 *                               delegate IO adapter (RandomAccessFileAdapter by default)
	 */
	public CachedIoAdapter(IoAdapter ioAdapter) {
		this(ioAdapter, DEFAULT_PAGE_SIZE, DEFAULT_PAGE_COUNT);
	}

	/**
	 * Creates an instance of CachedIoAdapter with a custom
	 * page size and page count.<br>
	 *  @param IoAdapter
	 *                               delegate IO adapter (RandomAccessFileAdapter by default)
	 *  @param pageSize 
	 *                               cache page size
	 *  @param pageCount
	 *                               allocated amount of pages  
	 */
	public CachedIoAdapter(IoAdapter ioAdapter, int pageSize, int pageCount) {
		_io = ioAdapter;
		_pageSize = pageSize;
		_pageCount = pageCount;
	}

	/**
	 * Creates an instance of CachedIoAdapter with extended parameters.<br>
	 *  @param path
	 *                              database file path
	 *  @param lockFile
	 *                              determines if the file should be locked 
	 *  @param initialLength
	 *                              initial file length, new writes will start from this point
	 *  @param IoAdapter 
	 *                              delegate IO adapter (RandomAccessFileAdapter by default)
	 *  @param pageSize
	 *                              cache page size
	 *  @param pageCount
	 *                              allocated amount of pages 
	 */
	public CachedIoAdapter(String path, boolean lockFile, long initialLength,
			IoAdapter io, int pageSize, int pageCount) throws IOException {
		_io = io;
		_pageSize = pageSize;
		_pageCount = pageCount;

		initCache();
		initIOAdaptor(path, lockFile, initialLength);

		_position = initialLength;
		_filePointer = initialLength;
		_fileLength = _io.getLength();
	}

	/**
	 * Creates and returns a new CachedIoAdapter <br>
	 *  @param path
	 *                              database file path
	 *  @param lockFile
	 *                              determines if the file should be locked 
	 *  @param initialLength
	 *                              initial file length, new writes will start from this point
	 */
	public IoAdapter open(String path, boolean lockFile, long initialLength)
			throws IOException {
		return new CachedIoAdapter(path, lockFile, initialLength, _io,
				_pageSize, _pageCount);
	}

	/**
	 * Deletes the database file
	 * @param path
	 *                              file path
	 */
	public void delete(String path) {
		_io.delete(path);
	}

	/**
	 * Checks if the file exists
	 * @param path
	 *                              file path
	 */
	public boolean exists(String path) {
		return _io.exists(path);
	}

	private void initIOAdaptor(String path, boolean lockFile, long initialLength)
			throws IOException {
		_io = _io.open(path, lockFile, initialLength);
	}

	private void initCache() {
		_head = new Page(_pageSize);
		_head.prev = null;
		Page page = _head;
		Page next = _head;
		for (int i = 0; i < _pageCount - 1; ++i) {
			next = new Page(_pageSize);
			page.next = next;
			next.prev = page;
			page = next;
		}
		_tail = next;
	}

	/**
	 * Reads the file into the buffer using pages from cache. 
	 * If the next page is not cached it will be read from the file.
	 * @param buffer
	 *                              destination buffer
	 * @param length
	 *                              how many bytes to read
	 */
	public int read(byte[] buffer, int length) throws IOException {
		long startAddress = _position;
		long endAddress = startAddress + length;
		Page page;
		int readLength;
		int bufferOffset = 0;
		while (startAddress < endAddress) {
			page = getPage(startAddress, true);
			readLength = (int) (_pageSize - startAddress % _pageSize);
			readLength = Math.min(buffer.length - bufferOffset, readLength);
			readLength = Math.min(length, readLength);
			page.read(buffer, bufferOffset, startAddress, readLength);
			movePageToHead(page);
			startAddress += readLength;
			bufferOffset += readLength;
		}
		_position = endAddress;
		return length;
	}

	/**
	 * Writes the buffer to cache using pages
	 * @param buffer
	 *                              source buffer
	 * @param length
	 *                              how many bytes to write
	 */
	public void write(byte[] buffer, int length) throws IOException {
		long startAddress = _position;
		long endAddress = startAddress + length;
		Page page = null;
		int writtenLength;
		int bufferOffset = 0;
		while (startAddress < endAddress) {
			writtenLength = (int) (_pageSize - startAddress % _pageSize);
			writtenLength = Math.min(writtenLength, length);
			writtenLength = Math.min(buffer.length - bufferOffset, writtenLength);
			// writtenLength == pageSize means it doesn't need to reload the
			// page from disk since the whole page will be written immediately.
			boolean load = writtenLength != _pageSize;
			page = getPage(startAddress, load);
			page.write(buffer, bufferOffset, startAddress, writtenLength);
			movePageToHead(page);
			startAddress += writtenLength;
			bufferOffset += writtenLength;
		}
		_position = endAddress;
		_fileLength = Math.max(page.startPosition + _pageSize, _fileLength);
	}

	/**
	 * Flushes cache to a physical storage
	 */
	public void sync() throws IOException {
		flushAllPages();
		_io.sync();
	}

	/**
	 * Returns the file length
	 */
	public long getLength() throws IOException {
		return _fileLength;
	}

	/**
	 * Flushes and closes the file
	 */
	public void close() throws IOException {
		flushAllPages();
		_io.close();
	}

	private Page getPage(long startAddress, boolean load) throws IOException {
		Page page;
		page = getPageFromCache(startAddress);
		if (page == null) {
			page = getFreePage();
			if (load) {
				loadPage(page, startAddress);
			} else {
				page.startPosition = startAddress;
			}
		}
		return page;
	}

	private Page getFreePage() throws IOException {
		if (!_tail.isFree()) {
			flushPage(_tail);
			// _posPageMap.remove(new Long(tail.startPosition / PAGE_SIZE));
		}
		return _tail;
	}

	private Page getPageFromCache(long pos) throws IOException {
		Page page = _head;
		while (page != null) {
			if (page.contains(pos)) {
				return page;
			}
			page = page.next;
		}
		return null;
		// Page page = (Page) _posPageMap.get(new Long(pos/PAGE_SIZE));
		// return page;
	}

	private void flushAllPages() throws IOException {
		Page node = _head;
		while (node != null) {
			flushPage(node);
			node = node.next;
		}
	}

	private void flushPage(Page page) throws IOException {
		if (!page.dirty) {
			return;
		}
		ioSeek(page.startPosition);
		writePage(page);
		return;
	}

	private void loadPage(Page page, long pos) throws IOException {
		page.startPosition = pos - pos % _pageSize;
		ioSeek(page.startPosition);
		int readCount = _io.read(page.buffer);
		if (readCount > 0) {
			_filePointer += readCount;
		}
		// _posPageMap.put(new Long(page.startPosition / PAGE_SIZE), page);
	}

	private void movePageToHead(Page page) {
		if (page == _head) {
			return;
		}
		if (page == _tail) {
			Page tempTail = _tail.prev;
			tempTail.next = null;
			_tail.next = _head;
			_tail.prev = null;
			_head.prev = page;
			_head = _tail;
			_tail = tempTail;
		} else {
			page.prev.next = page.next;
			page.next.prev = page.prev;
			page.next = _head;
			_head.prev = page;
			page.prev = null;
			_head = page;
		}
	}

	private void writePage(Page page) throws IOException {
		_io.write(page.buffer);
		_filePointer += _pageSize;
		page.dirty = false;
	}

	/**
	 * Moves the pointer to the specified file position
	 * @param pos
	 *                              position within the file								 
	 */
	public void seek(long pos) throws IOException {
		_position = pos;
		long endAddress = pos - pos % _pageSize + _pageSize;
		_fileLength = Math.max(_fileLength, endAddress);
	}

	private void ioSeek(long pos) throws IOException {
		if (_filePointer != pos) {
			_io.seek(pos);
			_filePointer = pos;
		}
	}

	private static class Page {

		public byte[] buffer;

		public long startPosition = -1;

		public int size;

		public boolean dirty;

		Page prev;

		Page next;

		public Page(int size) {
			buffer = new byte[size];
			this.size = size;
		}

		public void read(byte[] out, int outOffset, long startPosition,
				int length) {
			int bufferOffset = (int) (startPosition - this.startPosition);
			System.arraycopy(buffer, bufferOffset, out, outOffset, length);
		}

		public void write(byte[] data, int dataOffset, long startPosition,
				int length) {
			int bufferOffset = (int) (startPosition - this.startPosition);
			System.arraycopy(data, dataOffset, buffer, bufferOffset, length);
			dirty = true;
		}

		public boolean contains(long address) {
			if (startPosition != -1 && address >= startPosition
					&& address < startPosition + size) {
				return true;
			}
			return false;
		}

		public boolean isFree() {
			return startPosition == -1;
		}
	}
}
