/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench.logging.replay;

import java.io.*;

import com.db4o.bench.logging.*;
import com.db4o.io.*;


public class ReplayExample {

	/**
	 * Setting LOG_REPLAY to true will produce a log file named WRITE_LOG_FILE_NAME
	 * when replaying OPEN_LOG_FILE_NAME.
	 * This can be used to compare the original log with the replayed one.
	 */
	private static final boolean LOG_REPLAY = true;
	
	private static final String OPEN_LOG_FILE_NAME = "file_access.log";
	private static final String WRITE_LOG_FILE_NAME = "replay.log";
	private static final String DB_FILE_NAME = "replaytest.yap";
	
	private static final int LOG_MODE = LoggingIoAdapter.LOG_READ + LoggingIoAdapter.LOG_SYNC + LoggingIoAdapter.LOG_WRITE;
	
	
	
	public static void main(String[] args) {
		new ReplayExample();
				
	}
	
	public ReplayExample(){
		IoAdapter rafFactory = new RandomAccessFileAdapter();
		IoAdapter factory;
		if ( LOG_REPLAY ) {
			factory = new LoggingIoAdapter(rafFactory, WRITE_LOG_FILE_NAME, LOG_MODE);
		}
		else {
			factory = rafFactory;
		}
		
		IoAdapter io = factory.open(DB_FILE_NAME, false, 0, false);
		
		LogReplayer replayer = new LogReplayer(OPEN_LOG_FILE_NAME, io);
		replayer.replayLog();
	}

}
