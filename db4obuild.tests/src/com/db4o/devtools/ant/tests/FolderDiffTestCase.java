package com.db4o.devtools.ant.tests;

import db4ounit.extensions.*;
import db4ounit.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.db4o.devtools.ant.FilterFoldersInList;
import com.db4o.devtools.ant.FolderDiff;
import com.db4o.devtools.ant.SvnSync;

public class FolderDiffTestCase implements Db4oTestCase {
	private String emptyFolderPath;
	private String folder1Path; 
	private String folder2Path; 
	private String folder3Path;
	private String folderWithIgnoredSubFolders;
	
	public static void main(String []args){
		//TestRunner runner = new TestRunner(FolderDiffTestCase.class);
		//runner.run();
		
		FolderDiffTestCase ts = new FolderDiffTestCase();
		try {
			ts.setUp();			
			ts.runTests();			
			ts.tearDown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void runTests() {
		testCommonCases();
		testBoundaryConditions();
	}
	
	public void testCommonCases() {
		try {
			assertFolder(
					FolderDiff.diff(folder1Path, folder2Path),
					1,
					"-\\A\\f1.txt;"     +
					"+\\A\\f1_new.txt;" + 
					"+\\D\\f5.txt");                           
			
			assertFolder(
					FolderDiff.diff(folder2Path, folder1Path),				
					1,
					"-\\A\\f1_new.txt;" + 
			        "-\\D\\f5.txt;"     +                           
					"+\\A\\f1.txt");
			
			assertFolder(
					FolderDiff.diff(folder1Path, folder3Path), 
					1, 
					"-\\A\\f1.txt;"       +
					"-\\A\\B\\f3.txt;"    +
					"-\\A\\B\\C\\f4.txt;" +
					"+\\B\\f3.txt;"        +
			        "+\\A\\B\\C\\f1.txt");			
		}
		catch(IOException ioe){
			Assert.fail(ioe.toString());
		}
	}
	
	public void testBoundaryConditions() {
		try {
			assertFolder(FolderDiff.diff(emptyFolderPath, emptyFolderPath), 0, "");
			assertFolder(FolderDiff.diff(folder1Path, folder1Path), 0, "");
			assertFolder(
					FolderDiff.diff(emptyFolderPath, folder1Path),
					0,
					"+\\A\\f1.txt;"    +
					"+\\A\\f2.txt;"    +
					"+\\A\\B\\f3.txt;" +
					"+\\A\\B\\C\\f4.txt");
			
			assertFolder(
					FolderDiff.diff(folder1Path, emptyFolderPath),
					0,
					"-\\A\\f1.txt;"    +
					"-\\A\\f2.txt;"    +
					"-\\A\\B\\f3.txt;" +
					"-\\A\\B\\C\\f4.txt");
		}
		catch(IOException ioe) {
			Assert.fail(ioe.toString());
		}
	}
	
	public void testFolderFilter() {
		try {
			assertFolder(
					FolderDiff.diff(folder1Path, folderWithIgnoredSubFolders, new FilterFoldersInList(new String[] {".svn"}) ), 
					2, 
					"-\\A\\B\\C\\f4.txt;" +
					"+\\A\\f2.txt;"       +
					"+\\A\\f4.txt;"       +
					"+\\A\\f4.txt.new");
			
		}
		catch (IOException ioe) {
			Assert.fail("testFolderFilter failed: " + ioe.toString());
		}
	}
	
	private void assertFolder(FolderDiff diff, int changedCount, String expectedDiffs) {
		Assert.isNotNull(diff);
		Assert.areEqual(changedCount, diff.changedFiles().size());
		
		if (expectedDiffs.length() > 0) {
			String[] expectedDiffItems = expectedDiffs.split(";");
			for (String diffItem : expectedDiffItems) {
				switch(diffItem.charAt(0)) {
				case '+':
					assertFileAdded(diff, diffItem.substring(1));
					break;
					
				case '-':
					assertFileRemoved(diff, diffItem.substring(1));
					break;
				}
			}	
		}
	}

	private void assertFileRemoved(FolderDiff diff, String file) {
		Assert.areNotEqual(-1, diff.deletedFiles().indexOf(file));
	}

	private void assertFileAdded(FolderDiff diff, String file) {
		Assert.areNotEqual(-1, diff.newFiles().indexOf(file));
	}

	public void setUp() throws Exception {
		emptyFolderPath = createFolderStructure("emptyFolder", "");
		folder1Path = createFolderStructure(
						"Folder1",
						
						"A\\f1.txt(file1 contents);"+
						"A\\f2.txt(file2 contents);"+
						"A\\B\\f3.txt(file3 contents);" +
						"A\\B\\C\\f4.txt(file4 contents)");
		
		folder2Path = createFolderStructure(
						"Folder2",
						
						"A\\f1_new.txt(file1 new contents);"    + 
						"A\\f2.txt(file2 contents);"            + 
						"A\\B\\f3.txt(file3 changed contents);" + 
						"A\\B\\C\\f4.txt(file4 contents);"      + 
						"D\\f5.txt()");
		
		folder3Path = createFolderStructure(
						"Folder3", 
						
						"A\\f2.txt(contents file2);" +
						"B\\f3.txt(file3 contents);" +
						"A\\B\\C\\f1.txt(file1 contents)");
		
		folderWithIgnoredSubFolders = createFolderStructure(
											"FolderWithIgnoredSubFolders", 
											
											"A\\.svn\\f1.txt(file in svn folder!);"+
											"A\\.svn\\f5.txt(file in svn folder!);"+
											"A\\.svn\\f6.txt(file in svn folder!);"+
											"A\\.svn\\f7.txt(file in svn folder!);"+
											"A\\f1.txt(Fiona);"+
											"A\\f2.txt(file2 contents);"           +
											"A\\B\\f3.txt(contents file3);"        +
											"A\\f4.txt(new file1);"                +
											"A\\f4.txt.new(nee file 2");
	}

	private String createFolderStructure(String folderName, String folderStructure) throws IOException {
		String tempPath = getTempPath();
		String fullFolderPath = tempPath + "\\" + folderName;
		
		String []files = folderStructure.split(";");
		for(int i = 0; i < files.length; i++) {
			createFileContents(fullFolderPath, files[i]);
		}
		
		return fullFolderPath;
	}

	private String getTempPath() throws IOException {
		File tempFile = File.createTempFile("I Dont", ".care");
		String tempPath = tempFile.getParent();
		tempFile.delete();
		
		return tempPath;
	}

	private void createFileContents(String parent, String string) throws IOException {
		Pattern regexp = Pattern.compile("(.*)\\((.*)\\)");
		Matcher m = regexp.matcher(string);
		
		if (m.matches()){
			String filePath = parent + "\\" + m.group(1);
			String fileContents = m.group(2);
			
			createFolder(new File(filePath).getParent());
			createFile(filePath, fileContents);
		}
		else {
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
		FileWriter writer = null;
		try{
			writer = new FileWriter(filePath);
			writer.write(fileContents);
		}
		finally {
			if (writer != null){
				writer.close();
			}				
		}		
	}

	public void tearDown() throws Exception {
	}
}
