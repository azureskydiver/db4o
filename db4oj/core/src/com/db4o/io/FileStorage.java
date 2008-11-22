package com.db4o.io;

import java.io.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.internal.*;

public class FileStorage implements Storage {

	public Bin open(String uri, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
		return new FileBin(uri, lockFile, initialLength, readOnly);
    }

	public boolean exists(String uri) {
		final File file = new File(uri);
		return file.exists() && file.length() > 0;
    }
	
	static class FileBin implements Bin {

		private final String _path;

		private final RandomAccessFile _file;

		FileBin(String path, boolean lockFile,
				long initialLength, boolean readOnly) throws Db4oIOException {
			boolean ok = false;
			try {
				_path = new File(path).getCanonicalPath();
				_file = new RandomAccessFile(_path, readOnly ? "r" : "rw");
				if (initialLength > 0) {
					write(initialLength - 1, new byte[] { 0 }, 1);
				}
				if (lockFile) {
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
