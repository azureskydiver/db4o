package com.db4o.buildrename.orig;
import java.io.*;

public class Rename {
	
	public static void main(String[] args) throws IOException {
		if(args == null || args.length == 0){
			println("Pass the directory where the files are to be renamed as an argument.");
			return;
		}
		new Rename().run(args[0]);
		
	}

	private void run(String path) throws IOException {
		if(rename(path, false) == 0){
			return;
		}
		println();
		println("Hit y and Return to proceed.");
		int readByte = System.in.read();
		if(readByte != 'y'){
			println("Rename operation cancelled.");
			return;
		}
		rename(path, true);
	}

	private static void println(String msg) {
		System.out.println(msg);
	}

	private static void println() {
		System.out.println("");
	}

	private int rename(String path, boolean doRename) {
		File file = new File(path);
		File[] files = file.listFiles();
		int count = 0;
		for (int i = 0; i < files.length; i++) {
			if(rename(files[i], doRename)){
				count ++;
			}
		}
		println();
		println("Path: " + path);
		if(doRename){
			println("" + count + " files renamed.");
		} else {
			println("" + count + " files contain a long version name with 3 dots and can be renamed.");
		}
		
		return count;
	}

	private boolean rename(File file, boolean doRename) {
		String prepend = doRename ? "" : "? ";
		String oldPath = file.getAbsolutePath();
		String oldVersion = version(oldPath);
		if(oldVersion == null){
			return false;
		}
		if(! isLongVersion(oldVersion)){
			return false;
		}
		int index = indexOf(oldVersion, ".", 2);
		String newVersion = oldVersion.substring(0, index);
		String newPath = oldPath.replace(oldVersion, newVersion);
		
		println(prepend + "Rename " + oldPath);
		println(prepend + "To     " + newPath);
		println();
		if(doRename){
			file.renameTo(new File(newPath));
		}
		return true;
		
	}

	private int indexOf(String inString, String subString, int occurence) {
		int occurrences = 0;
		int index = 0;
		while(true){
			int indexOf = inString.indexOf(subString, index);
			occurrences++;
			if(occurrences == occurence){
				return indexOf;
			}
			index = indexOf + 1;
		}
	}

	private boolean isLongVersion(String version) {
		int occurrences = countOccurrences(version, ".");
		return occurrences == 3;
	}
	
	private String version(String path){
		int beginIndex = path.indexOf("-");
		if(beginIndex == -1){
			return null;
		}
		beginIndex ++;
		int endIndex = path.indexOf("-", beginIndex + 1);
		if(endIndex == -1){
			return null;
		}
		String version = path.substring(beginIndex, endIndex);
		return version;
	}

	private int countOccurrences(String inString, String subString) {
		int occurrences = 0;
		int index = 0;
		while(true){
			int indexOf = inString.indexOf(subString, index);
			if(indexOf == -1){
				return occurrences;
			}
			occurrences++;
			index = indexOf + 1;
		}
	}

}
