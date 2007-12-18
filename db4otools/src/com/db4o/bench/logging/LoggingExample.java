/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench.logging;

import java.io.*;

import com.db4o.*;
import com.db4o.io.*;



public class LoggingExample {

	private static final String LOG_FILE_NAME = "file_access.log";
	private static final String DB_FILE_NAME = "logtest.yap";

	
	private static final int LOG_MODE = LoggingIoAdapter.LOG_READ + LoggingIoAdapter.LOG_SYNC + LoggingIoAdapter.LOG_WRITE;
	
	
	public static void main(String[] args) {
		new LoggingExample();
	}

	public LoggingExample() {
		try {
			configureIo();
			ObjectContainer db = Db4o.openFile(DB_FILE_NAME);
			db.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void configureIo() throws FileNotFoundException {
		IoAdapter delegate = new RandomAccessFileAdapter();
		IoAdapter io = new LoggingIoAdapter(delegate, LOG_FILE_NAME, LOG_MODE);
		Db4o.configure().io(io);
	}

}
