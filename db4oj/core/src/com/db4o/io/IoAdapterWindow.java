/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.io;

/**
 * Bounded handle into an IoAdapter: Can only access a restricted area.
 */
public class IoAdapterWindow {

	private IoAdapter _io;
	private int _blockOff;
	private int _len;
	private boolean _disabled;

	/**
	 * @param io The delegate I/O adapter
	 * @param blockOff The block offset address into the I/O adapter that maps to the start index (0) of this window
	 * @param len The size of this window in bytes
	 */
	public IoAdapterWindow(IoAdapter io,int blockOff,int len) {
		_io = io;
		_blockOff=blockOff;
		_len=len;
		_disabled=false;
	}

	/**
	 * @return Size of this I/O adapter window in bytes.
	 */
	public int length() {
		return _len;
	}

	/**
	 * @param off Offset in bytes relative to the window start
	 * @param data Data to write into the window starting from the given offset
	 */
	public void write(int off,byte[] data) throws IllegalArgumentException, IllegalStateException{
		checkBounds(off, data);
		_io.blockSeek(_blockOff+off);
		_io.write(data);
	}

	/**
	 * @param off Offset in bytes relative to the window start
	 * @param data Data buffer to read from the window starting from the given offset
	 */
	public int read(int off,byte[] data) throws IllegalArgumentException, IllegalStateException {
		checkBounds(off, data);
		_io.blockSeek(_blockOff+off);
		return _io.read(data);
	}

	/**
	 * Disable IO Adapter Window
	 */
	public void disable() {
		_disabled=true;
	}
	
	/**
	 * Flush IO Adapter Window
	 */
	public void flush()  {
		if(!_disabled) {
			_io.sync();
		}
	}
	
	private void checkBounds(int off, byte[] data) {
		if(_disabled) {
			throw new IllegalStateException();
		}
		if(data==null||off<0||off+data.length>_len) {
			throw new IllegalArgumentException();
		}
	}
}
