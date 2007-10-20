/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench;

import java.io.*;

import com.db4o.*;
import com.db4o.io.*;



public class Logging {

	private static final int LOG_MODE = LoggingIoAdapter.LOG_READ + LoggingIoAdapter.LOG_SYNC + LoggingIoAdapter.LOG_WRITE;
	private static final String LOG_FILE_NAME = "file_access.log";
	private static final String DB_FILE_NAME = "logtest.yap";
	
	
	public static void main(String[] args) {
		try {
			configureIo();
			
			ObjectContainer db = Db4o.openFile(DB_FILE_NAME);
			
			//db.commit();
			db.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	private static void configureIo() throws FileNotFoundException {
		PrintStream out = new PrintStream(new FileOutputStream(LOG_FILE_NAME));
		IoAdapter delegate = new MemoryIoAdapter();
		IoAdapter io = new LoggingIoAdapter(delegate, out, LOG_MODE);
		Db4o.configure().io(io);
	}

}
