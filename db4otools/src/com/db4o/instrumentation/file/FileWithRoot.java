package com.db4o.instrumentation.file;

import java.io.*;

public class FileWithRoot implements Comparable {
	private final File _root;
	private final File _file;

	public FileWithRoot(File root, File file) {
		this._root = root;
		this._file = file;
	}
	
	public File root() {
		return _root;
	}

	public File file() {
		return _file;
	}

	public int hashCode() {
		return 43 * _root.hashCode() + _file.hashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final FileWithRoot other = (FileWithRoot) obj;
		return _root.equals(other._root) && _file.equals(other._file);
	}

	public int compareTo(Object o) {
		return _file.compareTo(((FileWithRoot)o)._file);
	}
	
	public String toString() {
		return _file + " [" + _root + "]";
	}
	
}
