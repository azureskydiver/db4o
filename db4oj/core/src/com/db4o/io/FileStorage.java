package com.db4o.io;

import java.io.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.internal.*;

public class FileStorage implements Storage {

	// TODO: replace readOnly by ReadOnlyStorage decorator
	public Bin open(String uri, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
		return new FileBin(uri, lockFile, initialLength, readOnly);
    }

	public boolean exists(String uri) {
		final File file = new File(uri);
		return file.exists() && file.length() > 0;
    }
	
	/**
	 * IO adapter for random access files.
	 */
	static class FileBin implements Bin {

		private final String _path;

		private final RandomAccessFile _delegate;

		FileBin(String path, boolean lockFile,
				long initialLength, boolean readOnly) throws Db4oIOException {
			boolean ok = false;
			try {
				_path = new File(path).getCanonicalPath();
				_delegate = new RandomAccessFile(_path, readOnly ? "r" : "rw");
				if (initialLength > 0) {
					write(initialLength - 1, new byte[] { 0 }, 1);
				}
				if (lockFile) {
					Platform4.lockFile(_path, _delegate);
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
				if (_delegate != null) {
					_delegate.seek(0);
				}
			} catch (IOException e) {
				// ignore
			}
			
			Platform4.unlockFile(_path, _delegate);
			try {
				if (_delegate != null) {
					_delegate.close();
				}
			} catch (IOException e) {
				throw new Db4oIOException(e);
			}
		}
		
		public long length() throws Db4oIOException {
			try {
				return _delegate.length();
			} catch (IOException e) {
				throw new Db4oIOException(e);
			}
		}

		public int read(long pos, byte[] bytes, int length) throws Db4oIOException {
			try {
				seek(pos);
				return _delegate.read(bytes, 0, length);
			} catch (IOException e) {
				throw new Db4oIOException(e);
			}
		}

		private void seek(long pos) throws IOException {

			if (DTrace.enabled) {
				DTrace.REGULAR_SEEK.log(pos);
			}
			_delegate.seek(pos);
		}

		public void sync() throws Db4oIOException {
			try {
				_delegate.getFD().sync();
			} catch (IOException e) {
				throw new Db4oIOException(e);
			}
		}

		public void write(long pos, byte[] buffer, int length) throws Db4oIOException {
			try {
				seek(pos);
				_delegate.write(buffer, 0, length);
			} catch (IOException e) {
				throw new Db4oIOException(e);
			}
		}
	}
}
