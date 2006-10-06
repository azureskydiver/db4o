/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.lib;

import java.io.*;

public class File4 {

    public static void copy(String source, String target) {
        try {
        	java.io.File sourceFile = new java.io.File(source);
        	
            java.io.File targetFile = new java.io.File(target);
			targetFile.mkdirs();
            targetFile.delete();
            
            if (sourceFile.isDirectory()) {
                copyDirectory(sourceFile, targetFile);
            } else {
                copyFile(sourceFile, targetFile);
            }
        } catch (Exception e) {
            System.out.println("File.copy failed.");
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

	public static void copyFile(File source, File target) throws IOException {
		final int bufferSize = 64000;

		RandomAccessFile rafIn = new RandomAccessFile(source.getAbsolutePath(), "r");
		RandomAccessFile rafOut = new RandomAccessFile(target.getAbsolutePath(), "rw");
		long len = rafIn.length();
		byte[] bytes = new byte[bufferSize];

		while (len > 0) {
		    len -= bufferSize;
		    if (len < 0) {
		        bytes = new byte[(int) (len + bufferSize)];
		    }
		    rafIn.read(bytes);
		    rafOut.write(bytes);
		}

		rafIn.close();
		rafOut.close();
	}

	private static void copyDirectory(File source, File target) throws IOException {
		String[] files = source.list();
		if (files != null) {
		    for (int i = 0; i < files.length; i++) {
		        copy(assemble(source.getAbsolutePath(), files[i]),
		        	assemble(target.getAbsolutePath(), files[i]));
		    }
		}
	}
	
    private static String assemble(String pathName, String fileName) {
        return String4._right(pathName, java.io.File.separator)
            ? pathName + fileName
            : pathName + java.io.File.separator + fileName;
    }

	public static void delete(String fname) {
		new File(fname).delete();
	}
    
    public static boolean exists(String fname){
        return new File(fname).exists();
    }
}
