/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench;

import java.io.*;

import com.db4o.io.*;


public class Replay {

	
	private static final int LOG_MODE = LoggingIoAdapter.LOG_READ + LoggingIoAdapter.LOG_SYNC + LoggingIoAdapter.LOG_WRITE;
	private static final String OPEN_LOG_FILE_NAME = "file_access.log";
	private static final String WRITE_LOG_FILE_NAME = "replay.log";
	private static final String DB_FILE_NAME = "replaytest.yap";
	
	public static void main(String[] args) {
		try {
			PrintStream out = new PrintStream(new FileOutputStream(WRITE_LOG_FILE_NAME));
			IoAdapter rafFactory = new RandomAccessFileAdapter();
			IoAdapter logFactory = new LoggingIoAdapter(rafFactory, out, LOG_MODE);
			IoAdapter io = logFactory.open(DB_FILE_NAME, false, 0, false);
			LogReplayer replayer = new LogReplayer(OPEN_LOG_FILE_NAME, io);
			replayer.replayLog();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
	
	

}
