/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench.logging;

import java.io.*;



public class BatchLogStatistics {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if ( args.length < 1 ) {
			System.out.println("[BATCH] No path given.");
		}
		else {
			new BatchLogStatistics(args[0]);
		}

	}

	
	public BatchLogStatistics(String logDirectoryPath) {
		File directory = new File(logDirectoryPath);
		if ( directory.isDirectory() ) {
			System.out.println("[BATCH] Creating statistics for logs in " + logDirectoryPath);
			FilenameFilter logFilter = new LogFilter();
			File[] logFiles = directory.listFiles(logFilter);
			
			int i;
			for ( i = 0; i < logFiles.length; i++ ) {
				new LogStatistics(logFiles[i].getPath());
			}
			
			System.out.println("[BATCH] Statistics for " + (++i) + " log files have been created!");
		}
		else {
			System.out.println("[BATCH] Given path is no directory!");
			System.out.println("[BATCH] Path: " + logDirectoryPath);
		}
	}
	
}


class LogFilter implements FilenameFilter {

	public boolean accept(File dir, String name) {
        return (name.toLowerCase().endsWith(".log"));
    }
	
}