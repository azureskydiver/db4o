package com.db4o.instrumentation.ant;

import java.util.*;

import org.apache.tools.ant.types.*;
import org.apache.tools.ant.types.resources.*;

import com.db4o.instrumentation.file.*;

/**
 * @exclude
 */
class AntFileSetPathRoot implements FilePathRoot {

	private FileSet[] _fileSets;

	public AntFileSetPathRoot(FileSet[] fileSets) {
		_fileSets = fileSets;
	}
	
	public Iterator files() {
		return new FileSetIterator(_fileSets);
	}

	public String[] rootDirs() {
		String[] rootDirs = new String[_fileSets.length];
		for (int rootIdx = 0; rootIdx < _fileSets.length; rootIdx++) {
			rootDirs[rootIdx] = _fileSets[rootIdx].getDir().getAbsolutePath();
		}
		return rootDirs;
	}

	private static class FileSetIterator implements Iterator {

		private final FileSet[] _fileSets;
		private int _fileSetIdx;
		private Iterator _fileSetIter;
		
		public FileSetIterator(FileSet[] fileSets) {
			_fileSets = fileSets;
			advanceFileSet();
		}

		public boolean hasNext() {
			return _fileSetIter.hasNext();
		}

		public Object next() {
			FileResource fileRes = (FileResource) _fileSetIter.next();
			return new FileWithRoot(fileRes.getBaseDir(), fileRes.getFile());
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		private void advanceFileSet() {
			while((_fileSetIter == null || !_fileSetIter.hasNext()) && _fileSetIdx < _fileSets.length) {
				_fileSetIter = _fileSets[_fileSetIdx].iterator();
				_fileSetIdx++;
			}
		}
	}
}
