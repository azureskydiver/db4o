/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.polepos.continuous;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class RevisionBasedMostRecentJarFileSelectionStrategy implements JarFileSelectionStrategy {

	private final static Pattern JAR_NAME_PATTERN = Pattern.compile("db4o.*(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)-.*\\.jar");
	private final static int REVISION_GROUP_IDX = 4;

	private final int _numOthers;
	
	public RevisionBasedMostRecentJarFileSelectionStrategy(int numOthers) {
		if(numOthers < 1) {
			throw new IllegalArgumentException();
		}
		_numOthers = numOthers;
	}

	public Db4oJarCollection select(File[] files) {
		File[] jars = filterDb4oJars(files);
		if(jars.length < (_numOthers + 1)) {
			throw new IllegalArgumentException("At least " + (_numOthers + 1) + " db4o Jars are required. Found: " + jars.length);
		}
		Arrays.sort(jars, new Db4oJarComparator(JAR_NAME_PATTERN, REVISION_GROUP_IDX));
		if(jars.length > _numOthers + 1) {
			File[] selectedJars = new File[_numOthers + 1];
			System.arraycopy(jars, 0, selectedJars, 0, _numOthers + 1); 
			jars = selectedJars;
		}
		return new Db4oJarCollection(jars);
	}

	private File[] filterDb4oJars(File[] files) {
		Set<File> db4oJars = new HashSet<File>();
		for (File file : files) {
			if(JAR_NAME_PATTERN.matcher(file.getName()).matches()) {
				db4oJars.add(file);
			}
		}
		return db4oJars.toArray(new File[db4oJars.size()]);
	}

	
	private static final class Db4oJarComparator implements Comparator<File> {
		
		private final Pattern _fileNamePattern;
		private final int _revisionGroupIdx;
		
		public Db4oJarComparator(Pattern fileNamePattern, int revisionGroupIdx) {
			_fileNamePattern = fileNamePattern;
			_revisionGroupIdx = revisionGroupIdx;
		}
		
		public int compare(File file1, File file2) {
			return ((Integer)revision(file2)).compareTo(revision(file1));
		}

		private int revision(File file) {
			Matcher matcher = _fileNamePattern.matcher(file.getName());
			if(!matcher.matches()) {
				throw new IllegalArgumentException("Not a db4o jar: " + file.getAbsolutePath());
			}
			return Integer.parseInt(matcher.group(_revisionGroupIdx));
		}
	}

}
