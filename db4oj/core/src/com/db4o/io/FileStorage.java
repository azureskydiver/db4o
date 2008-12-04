/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.io;

import java.io.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.internal.*;

/**
 * Storage adapter to store db4o database data to physical
 * files on hard disc. 
 */
public class FileStorage implements Storage {

	/**
	 * opens a {@link Bin} on the specified URI (file system path).
	 */
	public Bin open(BinConfiguration config) throws Db4oIOException {
		return new FileBin(config);
    }

	/**
	 * returns true if the specified file system path already exists.
	 */
	public boolean exists(String uri) {
		final File file = new File(uri);
		return file.exists() && file.length() > 0;
    }
	
	private static class FileBin implements Bin {

		private final String _path;

		private final RandomAccessFile _file;
		
		FileBin(BinConfiguration config) throws Db4oIOException {
			boolean ok = false;
			try {
				_path = new File(config.uri()).getCanonicalPath();
				_file = new RandomAccessFile(_path, config.readOnly() ? "r" : "rw");
				if (config.initialLength() > 0) {
					write(config.initialLength() - 1, new byte[] { 0 }, 1);
				}
				if (config.lockFile()) {
					Platform4.lockFile(_path, _file);
				} 
				ok = true;
			} catch (IOException e) {
				throw new Db4oIOException(e);
			} finally {
				if(!ok) {
					close();
				}
			}
		}

		public void close() throws Db4oIOException {
			
			// TODO: use separate subclass for Android with the fix
			// 
			// FIXME: This is a temporary quickfix for a bug in Android.
			//        Remove after Android has been fixed.
			try {
				if (_file != null) {
					_file.seek(0);
				}
			} catch (IOException e) {
				// ignore
			}
			
			Platform4.unlockFile(_path, _file);
			try {
				if (_file != null) {
					_file.close();
				}
			} catch (IOException e) {
				throw new Db4oIOException(e);
			}
		}
		
		public long length() throws Db4oIOException {
			try {
				return _file.length();
			} catch (IOException e) {
				throw new Db4oIOException(e);
			}
		}

		public int read(long pos, byte[] bytes, int length) throws Db4oIOException {
			try {
				seek(pos);
				return _file.read(bytes, 0, length);
			} catch (IOException e) {
				throw new Db4oIOException(e);
			}
		}

		private void seek(long pos) throws IOException {

			if (DTrace.enabled) {
				DTrace.REGULAR_SEEK.log(pos);
			}
			_file.seek(pos);
		}

		public void sync() throws Db4oIOException {
			try {
				_file.getFD().sync();
			} catch (IOException e) {
				throw new Db4oIOException(e);
			}
		}
		
		public int syncRead(long position, byte[] bytes, int bytesToRead) {
			return read(position, bytes, bytesToRead);
		}
		
		public void write(long pos, byte[] buffer, int length) throws Db4oIOException {
			try {
				seek(pos);
				_file.write(buffer, 0, length);
			} catch (IOException e) {
				throw new Db4oIOException(e);
			}
		}
	}
}
