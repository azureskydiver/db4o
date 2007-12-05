/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench;

import java.io.*;

import com.db4o.*;
import com.db4o.io.*;


public class BenchmarkExample {

	private static final String LOG_FILE_NAME = "db4o-benchmark.log";
	private static final String DB_FILE_NAME = "benchmark.db4o";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new BenchmarkExample();

	}
	
	public BenchmarkExample() {
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
		IoAdapter io = new BenchmarkIoAdapter(delegate, LOG_FILE_NAME);
		Db4o.configure().io(io);
	}

}
