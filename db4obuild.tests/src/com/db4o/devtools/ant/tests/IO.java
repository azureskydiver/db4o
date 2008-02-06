/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.devtools.ant.tests;

import java.io.*;
import java.util.regex.*;

import wheel.io.*;


public class IO {

	private static final String RESOURCE_PREFIX = "resource:";

	public static String getTempPath() throws IOException {
		return new File(System.getProperty("java.io.tmpdir")).getCanonicalPath();
	}

	public static void createFolder(String path) {
		File targetFolder = new File(path);
		if (!targetFolder.exists())	{
			targetFolder.mkdirs();
		}
	}

	static final Pattern FILE_CONTENTS_REGEX = Pattern.compile("(.*)\\((.*)\\)");

	public static void createFile(String filePath, String fileContents) throws IOException {
		ensureParent(filePath);
		writeFile(filePath, fileContents);		
	}

	private static void writeFile(String filePath, String fileContents) throws IOException {
		FileWriter writer = new FileWriter(filePath);
		try {
			writer.write(fileContents);
		} finally {
			writer.close();
		}
	}

	private static void ensureParent(String filePath) {
		createFolder(new File(filePath).getParent());
	}

	public static void createFileContents(String parent, String fileReference) throws IOException {
		
		if (fileReference.startsWith(IO.RESOURCE_PREFIX)) {
			createFileFromResource(parent, fileReference.substring(IO.RESOURCE_PREFIX.length()));
			return;
		}
		
		Matcher m = FILE_CONTENTS_REGEX.matcher(fileReference);
		if (m.matches()) {
			String filePath = combine(parent, m.group(1));
			String fileContents = m.group(2);
			createFile(filePath, fileContents);
		} else {
			createFolder(parent);
		}
	}

	private static String combine(final String parent, final String fname) {
		return parent + "/" + fname;
	}

	private static void createFileFromResource(String parent, String resource) throws IOException {
		final String contents = ResourceLoader.getStringContents(IO.class, resource);
		createFile(combine(parent, resource), contents);
	}

	public static String createFolderStructure(String folderName, String... files) throws IOException {
		String tempPath = getTempPath();
		String fullFolderPath = combine(tempPath, folderName);
		
		for(int i = 0; i < files.length; i++) {
			createFileContents(fullFolderPath, files[i]);
		}
		
		return fullFolderPath;
	}

}
