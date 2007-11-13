package com.db4o.devtools.ant;

import java.io.*;
import java.util.*;

/**
 * FolderDiff class.<br><br>
 * 
 * Class used to get the difference between two folders. This class do not take
 * empty directories into account in order to compute the difference, so, if the
 * only difference between the two directories are empty folders this class will
 * report <b>no difference</b>.
 *
 */
public final class FolderDiff {
	
	private static final FolderFilter INCLUDE_ALL_FOLDERS = new FolderFilter() {
		public boolean filter(String path) {
			return false;
		}
	};
	
	private final Set<String> _changed;
	private final Set<String> _deleted;
	private final Set<String> _new;
	
	private final String _sourceFolder;
	private final String _compareToFolder;
	
	private FolderDiff(String source, String compareTo, Set<String> changedFiles, Set<String> deletedFiles, Set<String> newFiles) {
		_changed = Collections.unmodifiableSet(changedFiles);
		_deleted = Collections.unmodifiableSet(deletedFiles);
		_new = Collections.unmodifiableSet(newFiles);
		
		_sourceFolder = source;
		_compareToFolder = compareTo;
	}

	/**
	 * 
	 * @param from	
	 * @param to
	 * @param filter an object that specifies which children folders should be processed
	 * and which should not. To process all subfolders user INCLUDE_ALL_FOLDERS constant.
	 * @return a reference to a FolderDiff object that represents the difference
	 * between source and compareTo folders.
	 * @throws IOException
	 */
	public static FolderDiff diff(String from, String to, FolderFilter filter) throws IOException {
		Set<String> fromFiles = allFiles(from, filter);
		Set<String> toFiles = allFiles(to, filter);
		
		Set<String> changedCandidates = intersection(fromFiles, toFiles);
		
		Set<String> deletedFiles = disjunction(fromFiles, toFiles);		
		Set<String> newFiles = disjunction(toFiles, fromFiles);
		
		Set<String> changedFiles = getChangedFilesFromCandidates(changedCandidates, from, to);
		
		return new FolderDiff(from, to, changedFiles, deletedFiles, newFiles);
	}

	public Set<String> changedFiles(){
		return _changed;
	}
	
	public Set<String> deletedFiles(){
		return _deleted;
	}
	
	public Set<String> newFiles(){
		return _new;
	}
	
	public String sourceFolder() {
		return _sourceFolder;
	}
	
	public String compareToFolder() {
		return _compareToFolder;
	}
	
	public static FolderDiff diff(String source, String compareTo) throws IOException {
		return diff(source, compareTo, INCLUDE_ALL_FOLDERS);
	}
	
	private static HashSet<String> allFiles(String source, FolderFilter folderFilter) throws IOException {
		final HashSet<String> files = new HashSet<String>();
		internalGetFiles(source.length(), new File(source), files, folderFilter);
		return files;
	}

	private static void internalGetFiles(int baseFolderLen, File folder, HashSet<String> files, FolderFilter folderFilter) throws IOException {
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				if (!folderFilter.filter(file.getCanonicalPath())) {
					internalGetFiles(baseFolderLen, file, files, folderFilter);
				}
			} else {
				files.add(file.getCanonicalPath().substring(baseFolderLen));
			}
		}
	}

	private static Set<String> getChangedFilesFromCandidates(Set<String> changedCandidates, String source, String compareToBasePath) throws IOException {
		Set<String> changedFiles = new HashSet<String>(changedCandidates);
		for (String candidate : changedCandidates) {
			if (!sameFile(source + candidate, compareToBasePath + candidate)) {
				changedFiles.remove(candidate);
			}
		}
		
		return changedFiles;
	}

	private static boolean sameFile(String lhs, String rhs) throws IOException {
		File f1 = new File(rhs);
		File f2 = new File(lhs);
		return sameSize(f1, f2) && sameContents(f1, f2);
	}

	private static boolean sameSize(File f1, File f2) {
		return f1.length() == f2.length();
	}

	private static boolean sameContents(File lhs, File rhs) throws IOException {
		InputStream lin = new BufferedInputStream(new FileInputStream(lhs));
		try {
			InputStream rin = new BufferedInputStream(new FileInputStream(rhs));
			try {
				return sameContents(lin, rin);
			} finally {
				rin.close();
			}
		} finally {
			lin.close();
		}
	}

	private static boolean sameContents(InputStream lin, InputStream rin) throws IOException {
		int value = 0;
		while (-1 != (value = lin.read())) {
			if (value != rin.read()) {
				return false;
			}
		}
		return true;
	}
	
	private static Set<String> intersection(Set<String> set1, Set<String> set2) {
		HashSet<String> intersection = new HashSet<String>(set1);
		intersection.retainAll(set2);
		return intersection;
	}
	
	private static Set<String> disjunction(Set<String> source, Set<String> removed) {
		final HashSet<String> disjunction = new HashSet<String>(source);
		disjunction.removeAll(removed);
		return disjunction;
	}
}
