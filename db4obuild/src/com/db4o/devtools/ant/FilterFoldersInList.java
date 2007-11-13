package com.db4o.devtools.ant;

import java.util.Arrays;
import java.util.HashSet;

public class FilterFoldersInList implements FolderFilter {

	private HashSet<String> filteredFolders;
	
	public FilterFoldersInList(String [] folders) {
		filteredFolders = new HashSet<String>(Arrays.asList(folders));
	}
	
	public boolean filter(String path) {
		return filteredFolders.contains(lastFolderInPath(path));
	}

	private String lastFolderInPath(String path) {
		int index =path.lastIndexOf('\\');
		if ( index > 0) {
			path = path.substring(index + 1);
		}
		return path;
	}
}
