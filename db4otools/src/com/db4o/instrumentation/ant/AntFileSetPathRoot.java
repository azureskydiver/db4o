package com.db4o.instrumentation.ant;

import java.io.*;
import java.util.*;

import org.apache.tools.ant.types.*;
import org.apache.tools.ant.types.resources.*;

import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.file.*;

/**
 * @exclude
 */
class AntFileSetPathRoot implements FilePathRoot, ClassFilter {

	private FileSet[] _fileSets;

	public AntFileSetPathRoot(FileSet[] fileSets) {
		_fileSets = fileSets;
	}
	
	public Iterator files() {
		return new FileSetIterator(_fileSets);
	}

	public String[] rootDirs() throws IOException {
		String[] rootDirs = new String[_fileSets.length];
		for (int rootIdx = 0; rootIdx < _fileSets.length; rootIdx++) {
			FileSet curFileSet = _fileSets[rootIdx];
			File rootDir = curFileSet.getDir();
			if(rootDir == null && (curFileSet instanceof ZipFileSet)) {				
				ZipFileSet zipFileSet = (ZipFileSet)curFileSet;
				rootDir = zipFileSet.getSrc();
			}
			if(rootDir == null) {				
				rootDir = File.listRoots()[0]; // XXX
			}
			rootDirs[rootIdx] = rootDir.getCanonicalPath();
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
			Resource resource = (Resource)_fileSetIter.next();
			advanceFileSet();
			if(resource instanceof FileResource) {
				FileResource fileRes = (FileResource)resource;
				return new FileInstrumentationClassSource(fileRes.getBaseDir(), fileRes.getFile());
			}
			return new AntJarEntryInstrumentationClassSource(resource);
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

	public boolean accept(Class clazz) {
		try {
			for (Iterator fileSetIter = files(); fileSetIter.hasNext();) {
				InstrumentationClassSource source = (InstrumentationClassSource) fileSetIter.next();
				if(clazz.getName().equals(source.className())) {
					return true;
				}
			}
		}
		catch (IOException exc) {
			// FIXME
			throw new RuntimeException(exc.getMessage());
		}
		return false;
	}
}
