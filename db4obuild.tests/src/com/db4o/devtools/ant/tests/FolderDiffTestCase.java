package com.db4o.devtools.ant.tests;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import com.db4o.devtools.ant.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class FolderDiffTestCase implements Db4oTestCase {

	private String folder1Path; 
	private String folder2Path; 
	private String folder3Path;
	
	public void setUp() throws Exception {
		
		folder1Path = createFolderStructure(
						"Folder1",
						"A/f1.txt(file1 contents)",
						"A/f2.txt(file2 contents)",
						"A/B/f3.txt(file3 contents)",
						"A/B/C/f4.txt(file4 contents)");
		
		folder2Path = createFolderStructure(
						"Folder2",
						
						"A/f1_new.txt(file1 new contents)", 
						"A/f2.txt(file2 contents)", 
						"A/B/f3.txt(file3 changed contents)", 
						"A/B/C/f4.txt(file4 contents)", 
						"D/f5.txt()");
		
		folder3Path = createFolderStructure(
						"Folder3", 
						
						"A/f2.txt(contents file2)",
						"B/f3.txt(file3 contents)",
						"A/B/C/f1.txt(file1 contents)");
		
	}
	
	public void testCommonCases() throws Throwable {
		assertFolder(
				FolderDiff.diff(folder1Path, folder2Path),
				"-/A/f1.txt",
				"+/A/f1_new.txt", 
				"+/D/f5.txt",
				"C/A/B/f3.txt");                           
		
		assertFolder(
				FolderDiff.diff(folder2Path, folder1Path),
				"-/A/f1_new.txt", 
		        "-/D/f5.txt",                           
				"+/A/f1.txt",
				"C/A/B/f3.txt");
		
		assertFolder(
				FolderDiff.diff(folder1Path, folder3Path),
				"-/A/f1.txt",
				"-/A/B/f3.txt",
				"-/A/B/C/f4.txt",
				"+/B/f3.txt",
		        "+/A/B/C/f1.txt");			
	}
	
	public void testBoundaryConditions() throws Throwable {
		final String emptyFolderPath = createFolderStructure("emptyFolder");
		assertFolder(FolderDiff.diff(emptyFolderPath, emptyFolderPath));
		assertFolder(FolderDiff.diff(folder1Path, folder1Path));
		assertFolder(
				FolderDiff.diff(emptyFolderPath, folder1Path),
				"+/A/f1.txt",
				"+/A/f2.txt",
				"+/A/B/f3.txt",
				"+/A/B/C/f4.txt");
		
		assertFolder(
				FolderDiff.diff(folder1Path, emptyFolderPath),
				"-/A/f1.txt",
				"-/A/f2.txt",
				"-/A/B/f3.txt",
				"-/A/B/C/f4.txt");
	}
	
	public void testFolderFilter() throws Throwable {
		
		final String folderWithIgnoredSubFolders = createFolderStructure(
				"FolderWithIgnoredSubFolders", 
				
				"A/.svn/f1.txt(file in svn folder!)",
				"A/.svn/f5.txt(file in svn folder!)",
				"A/.svn/f6.txt(file in svn folder!)",
				"A/.svn/f7.txt(file in svn folder!)",
				"A/f1.txt(Fiona)",
				"A/f2.txt(file2 contents)",
				"A/B/f3.txt(contents file3)",
				"A/f4.txt(new file1)",
				"A/f4.txt.new(new file 2");

		assertFolder(
				FolderDiff.diff(folder1Path, folderWithIgnoredSubFolders, new FilterFoldersInList(new String[] {".svn"}) ),
				"-/A/B/C/f4.txt",
				"+/A/f4.txt",
				"+/A/f4.txt.new");
	}
	
	private void assertFolder(FolderDiff diff, String ... expectedDiffs) {
		Assert.isNotNull(diff);
		
		if (expectedDiffs.length == 0) {
			assertEmpty(diff.changedFiles());
			assertEmpty(diff.deletedFiles());
			assertEmpty(diff.newFiles());
			return;
		}
		
		for (String diffItem : expectedDiffs) {
			switch (diffItem.charAt(0)) {
				case '+':
					assertFileAdded(diff, normalizePath(diffItem.substring(1)));
					break;
					
				case '-':
					assertFileRemoved(diff, normalizePath(diffItem.substring(1)));
					break;
					
				case 'C':
					assertFileChanged(diff, normalizePath(diffItem.substring(1)));
			}
		}
	}

	private void assertEmpty(final List<String> files) {
		Assert.isTrue(files.isEmpty());
	}

	private String normalizePath(String substring) {
		return substring.replace('/', File.separatorChar);
	}
	
	private void assertFileChanged(FolderDiff diff, String file) {
		assertContains(file, diff.changedFiles(), "C");
	}

	private void assertFileRemoved(FolderDiff diff, String file) {
		assertContains(file, diff.deletedFiles(), "-");
	}

	private void assertFileAdded(FolderDiff diff, String file) {
		assertContains(file, diff.newFiles(), "+");
	}

	private void assertContains(String expectedFile, final List<String> files, final String operation) {
		Assert.isTrue(files.contains(expectedFile), "Expecting '" + operation + expectedFile + "'");
	}

	

	
	private String createFolderStructure(String folderName, String... files) throws IOException {
		String tempPath = getTempPath();
		String fullFolderPath = tempPath + "/" + folderName;
		
		for(int i = 0; i < files.length; i++) {
			createFileContents(fullFolderPath, files[i]);
		}
		
		return fullFolderPath;
	}

	private String getTempPath() throws IOException {
		return new File(System.getProperty("java.io.tmpdir")).getCanonicalPath();
	}

	private void createFileContents(String parent, String string) throws IOException {
		Pattern regexp = Pattern.compile("(.*)\\((.*)\\)");
		Matcher m = regexp.matcher(string);
		
		if (m.matches()){
			String filePath = parent + "/" + m.group(1);
			String fileContents = m.group(2);
			
			createFolder(new File(filePath).getParent());
			createFile(filePath, fileContents);
		} else {
			createFolder(parent);
		}
	}

	private void createFolder(String path) {
		File targetFolder = new File(path);
		if (!targetFolder.exists())	{
			targetFolder.mkdirs();
		}
	}

	private void createFile(String filePath, String fileContents) throws IOException {
		FileWriter writer = new FileWriter(filePath);
		try {
			writer.write(fileContents);
		} finally {
			writer.close();
		}		
	}

	public void tearDown() throws Exception {
	}
}
