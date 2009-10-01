/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.polepos.continuous;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class RevisionBasedMostRecentJarFileSelectionStrategy implements JarFileSelectionStrategy {

	private final static Pattern JAR_NAME_PATTERN = Pattern.compile("db4o.*(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)-.*\\.jar");
	private final static int REVISION_GROUP_IDX = 4;

	private final int[] _selectedIndices;
	
	public RevisionBasedMostRecentJarFileSelectionStrategy(int... selectedIndices) {
		if(selectedIndices.length < 1) {
			throw new IllegalArgumentException();
		}
		for (int selIdx : selectedIndices) {
			if(selIdx <= 0) {
				throw new IllegalArgumentException();
			}
		}
		Arrays.sort(selectedIndices);
		_selectedIndices = selectedIndices;
	}

	public Db4oJarCollection select(File[] files) {
		return new Db4oJarCollection(selectJars(collectMatchingJars(files)));
	}

	private List<File> selectJars(File[] matchingJars) {
		List<File> selectedJars = new ArrayList<File>(_selectedIndices.length);
		selectedJars.add(matchingJars[0]);
		for (int selIdxIdx = 0; selIdxIdx < _selectedIndices.length; selIdxIdx++) {
			int selIdx = _selectedIndices[selIdxIdx];
			if(selIdx >= matchingJars.length) {
				File oldestJar = matchingJars[matchingJars.length - 1];
				if(!selectedJars.contains(oldestJar)) {
					selectedJars.add(oldestJar);
				}
				break;
			}
			selectedJars.add(matchingJars[selIdx]);
		}
		return selectedJars;
	}

	private File[] collectMatchingJars(File[] files) {
		File[] matchingJars = filterDb4oJars(files);
		if(matchingJars.length < 2) {
			throw new IllegalArgumentException("At least 2 db4o Jars are required. Found: " + matchingJars.length);
		}
		Arrays.sort(matchingJars, new Db4oJarComparator(JAR_NAME_PATTERN, REVISION_GROUP_IDX));
		return matchingJars;
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
