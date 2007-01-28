/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.io;

import java.io.IOException;

/**
 * IO adapter for random access files.
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

	public CachedIoAdapter(IoAdapter ioAdapter) {
		this(ioAdapter, DEFAULT_PAGE_SIZE, DEFAULT_PAGE_COUNT);
	}

	public CachedIoAdapter(IoAdapter ioAdapter, int pageSize, int pageCount) {
		_io = ioAdapter;
		_pageSize = pageSize;
		_pageCount = pageCount;
	}

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

	public IoAdapter open(String path, boolean lockFile, long initialLength)
			throws IOException {
		return new CachedIoAdapter(path, lockFile, initialLength, _io,
				_pageSize, _pageCount);
	}

	public void delete(String path) {
		_io.delete(path);
	}

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
		Page next = null;
		for (int i = 0; i < _pageCount - 1; ++i) {
			next = new Page(_pageSize);
			page.next = next;
			next.prev = page;
			page = next;
		}
		_tail = next;
	}

	public int read(byte[] buffer, int length) throws IOException {
		long startAddress = _position;
		long endAddress = startAddress + length;
		Page page;
		int readLength;
		int bufferOffset = 0;
		while (startAddress < endAddress) {
			page = getPage(startAddress);
			readLength = (int) (_pageSize - startAddress % _pageSize);
			page.read(buffer, bufferOffset, startAddress, readLength);
			movePageToHead(page);
			startAddress += readLength;
			bufferOffset += readLength;
		}
		_position = endAddress;
		return length;
	}

	public void write(byte[] buffer, int length) throws IOException {
		long startAddress = _position;
		long endAddress = startAddress + length;
		Page page = null;
		int writtenLength;
		int bufferOffset = 0;
		while (startAddress < endAddress) {
			page = getPage(startAddress);
			writtenLength = (int) (_pageSize - startAddress % _pageSize);
			writtenLength = Math.min(writtenLength, length);
			page.write(buffer, bufferOffset, startAddress, writtenLength);
			movePageToHead(page);
			startAddress += writtenLength;
			bufferOffset += writtenLength;
		}
		_position = endAddress;
		_fileLength = Math.max(page.startPosition + _pageSize, _fileLength);
	}

	public void sync() throws IOException {
		flushAllPages();
		_io.sync();
	}

	public long getLength() throws IOException {
		return _fileLength;
	}

	public void close() throws IOException {
		flushAllPages();
		_io.close();
	}

	private Page getPage(long startAddress) throws IOException {
		Page page;
		page = getPageFromCache(startAddress);
		if (page == null) {
			page = getFreePage();
			loadPage(page, startAddress);
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
			int bufferRemaining = out.length - outOffset;
			int readLength = Math.min(bufferRemaining, length);
			System.arraycopy(buffer, bufferOffset, out, outOffset, readLength);
		}

		public void write(byte[] data, int dataOffset, long startPosition,
				int length) {
			int bufferOffset = (int) (startPosition - this.startPosition);
			int dataLength = data.length - dataOffset;
			int writeLength = Math.min(dataLength, length);
			System.arraycopy(data, dataOffset, buffer, bufferOffset,
					writeLength);
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
