/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.io;

import java.io.IOException;

/**
 * IO adapter for random access files.
 */
public class CachedIoAdapter extends IoAdapter {

	public Page head;

	public Page tail;

	public long _position;

	private int _pageSize = 1024;

	private int _pageCount = 64;

	private long _fileLength;

	private long _filePointer;

	private IoAdapter _io;

	// private Hashtable4 _posPageMap = new Hashtable4(PAGE_COUNT);

	public CachedIoAdapter(IoAdapter ioAdapter) {
		_io = ioAdapter;
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
		head = new Page(_pageSize);
		head.prev = null;
		Page page = head;
		Page next = null;
		for (int i = 0; i < _pageCount - 1; ++i) {
			next = new Page(_pageSize);
			page.next = next;
			next.prev = page;
			page = next;
		}
		tail = next;
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
			writtenLength = Util.min(writtenLength, length);
			page.write(buffer, bufferOffset, startAddress, writtenLength);
			movePageToHead(page);
			startAddress += writtenLength;
			bufferOffset += writtenLength;
		}
		_position = endAddress;
		_fileLength = Util.max(page.startPosition + _pageSize, _fileLength);
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
		if (!tail.isFree()) {
			flushPage(tail);
			// _posPageMap.remove(new Long(tail.startPosition / PAGE_SIZE));
		}
		return tail;
	}

	private Page getPageFromCache(long pos) throws IOException {
		Page page = head;
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
		Page node = head;
		while (node != null) {
			flushPage(node);
			node = node.next;
		}
	}

	public void flushPage(Page page) throws IOException {
		if (!page.dirty) {
			return;
		}
		ioSeek(page.startPosition);
		writePage(page);
		return;
	}

	public void loadPage(Page page, long pos) throws IOException {
		page.startPosition = pos - pos % _pageSize;
		ioSeek(page.startPosition);
		int readCount = _io.read(page.buffer);
		if (readCount > 0) {
			_filePointer += readCount;
		}
		// _posPageMap.put(new Long(page.startPosition / PAGE_SIZE), page);
	}

	private void movePageToHead(Page page) {
		if (page == head) {
			return;
		}
		if (page == tail) {
			Page tempTail = tail.prev;
			tempTail.next = null;
			tail.next = head;
			tail.prev = null;
			head.prev = page;
			head = tail;
			tail = tempTail;
		} else {
			page.prev.next = page.next;
			page.next.prev = page.prev;
			page.next = head;
			head.prev = page;
			page.prev = null;
			head = page;
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
		_fileLength = Util.max(_fileLength, endAddress);
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

		private boolean dirty;

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
			int readLength = Util.min(bufferRemaining, length);
			System.arraycopy(buffer, bufferOffset, out, outOffset, readLength);
		}

		public void write(byte[] data, int dataOffset, long startPosition,
				int length) {
			int bufferOffset = (int) (startPosition - this.startPosition);
			int dataLength = data.length - dataOffset;
			int writeLength = Util.min(dataLength, length);
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

	static class Util {
		public static int min(int a, int b) {
			return a < b ? a : b;
		}

		public static long max(long a, long b) {
			return a > b ? a : b;
		}
	}

}
