package com.db4o.devtools.ant;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;

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
	private static final FolderFilter INCLUDE_ALL_FOLDERS = null;
	private List<String> _changed;
	private List<String> _deleted;
	private List<String> _new;
	
	private String _sourceFolder;
	private String _compareToFolder;
	
	private FolderDiff(String source, String compareTo, HashSet<String> changedFiles, HashSet<String> deletedFiles, HashSet<String> newFiles) {
		_changed = new ArrayList<String>(changedFiles);
		_deleted = new ArrayList<String>(deletedFiles);
		_new = new ArrayList<String>(newFiles);
		
		_sourceFolder = source;
		_compareToFolder = compareTo;
	}

	/**
	 * 
	 * @param source	
	 * @param compareTo
	 * @param folderFilter an object that specifies which children folders should be processed
	 * and which should not. To process all subfolders user INCLUDE_ALL_FOLDERS constant.
	 * @return a reference to a FolderDiff object that represents the difference
	 * between source and compareTo folders.
	 * @throws IOException
	 */
	public static FolderDiff diff(String source, String compareTo, FolderFilter folderFilter) throws IOException {
		HashSet<String> filesInSource = getFiles(source, folderFilter);
		HashSet<String> filesInComparand = getFiles(compareTo, folderFilter);
		
		HashSet<String> changedCandidates = getChangedCandidates(filesInSource, filesInComparand);
		
		HashSet<String> deletedFiles = filesInSource;
		deletedFiles.removeAll(changedCandidates);
		
		HashSet<String> newFiles = filesInComparand;
		newFiles.removeAll(changedCandidates);
		
		HashSet<String> changedFiles = getChangedFilesFromCandidates(changedCandidates, source, compareTo);
		
		return new FolderDiff(source, compareTo, changedFiles, deletedFiles, newFiles);
	}
	
	public List<String> changedFiles(){
		return _changed;
	}
	
	public List<String> deletedFiles(){
		return _deleted;
	}
	
	public List<String> newFiles(){
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
	
	private static HashSet<String> getFiles(String source, FolderFilter folderFilter) {
		File folder = new File(source);
		HashSet<String> files = new HashSet<String>();
		
		internalGetFiles(source.length(), folder, files, folderFilter);
		
		return files;
	}

	private static void internalGetFiles(int baseFolderLen, File folder, HashSet<String> files, FolderFilter folderFilter) {
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				if (folderFilter == null || !folderFilter.filter(file.getAbsolutePath())) {
					internalGetFiles(baseFolderLen, file, files, folderFilter);
				}
			}
			else {
				files.add(file.getAbsolutePath().substring(baseFolderLen));
			}
		}
	}

	private static HashSet<String> getChangedFilesFromCandidates(HashSet<String> changedCandidates, String source, String compareToBasePath) throws IOException {
		HashSet<String> changedFiles = (HashSet<String>) changedCandidates.clone();
		for (String candidate : changedCandidates) {
			if (compareFileContents(source + candidate, compareToBasePath + candidate) == 0) {
				changedFiles.remove(candidate);
			}
		}
		
		return changedFiles;
	}

	private static long compareFileContents(String lhs, String rhs) throws IOException {
		File f1 = new File(rhs);
		File f2 = new File(lhs);

		long ret = compareFileLength(f1, f2);
		if (ret != 0) {
			return ret;
		}

		return compareFileContents(f1, f2);
	}

	private static long compareFileContents(File lhs, File rhs) throws IOException {
		FileChannel lhsChannel = null;
		FileChannel rhsChannel = null;
		
		try {
			lhsChannel = new RandomAccessFile(lhs, "r").getChannel();
			rhsChannel = new RandomAccessFile(rhs, "r").getChannel();
			
			MappedByteBuffer lhsBuffer = lhsChannel.map(FileChannel.MapMode.READ_ONLY, 0, lhs.length());
			MappedByteBuffer rhsBuffer = rhsChannel.map(FileChannel.MapMode.READ_ONLY, 0, rhs.length());
			
			return lhsBuffer.compareTo(rhsBuffer);
		}
		finally {
			if (lhsChannel != null) {
				lhsChannel.close();
			}
			
			if (rhsChannel != null) {
				rhsChannel.close();
			}
		}		
	}

	private static long compareFileLength(File lhs, File rhs) {
		long lhsLength = lhs.length();
		long rhsLength = rhs.length();
		
		return lhsLength - rhsLength;
	}

	private static HashSet<String> getChangedCandidates(HashSet<String> filesInSource, HashSet<String> filesInComparand) {
		HashSet<String> changedCandidates = (HashSet<String>) filesInSource.clone();
		changedCandidates.retainAll(filesInComparand);
		return changedCandidates;
	}
}
