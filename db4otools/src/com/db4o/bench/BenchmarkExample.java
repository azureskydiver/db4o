/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench;

import java.io.*;

import com.db4o.*;
import com.db4o.bench.logging.replay.*;
import com.db4o.io.*;


public class BenchmarkExample {

	private static final String LOG_FILE_NAME = "db4o-benchmark.log";
	private static final String DB_FILE_NAME = "benchmark.db4o";
	public static final String REPLAY_LOG_FILE_NAME = "simplecrud_10000.log";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// new BenchmarkExample().minimalRun();
		new BenchmarkExample().extendedRun();
		System.out.println("Statistics written to " + LOG_FILE_NAME);
	}

	private void minimalRun() {
		try {
			configureMinimalIo();
			ObjectContainer db = Db4o.openFile(DB_FILE_NAME);
			db.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	private void configureMinimalIo() throws FileNotFoundException {
		IoAdapter delegate = new RandomAccessFileAdapter();
		IoAdapter io = new BenchmarkIoAdapter(delegate, LOG_FILE_NAME, 1, false);
		Db4o.configure().io(io);
	}
	
	private void extendedRun() {
		IoAdapter rafFactory = new RandomAccessFileAdapter();
		IoAdapter bmFactory = new BenchmarkIoAdapter(rafFactory, LOG_FILE_NAME, 1, false);
		BenchmarkIoAdapter io = (BenchmarkIoAdapter) bmFactory.open(DB_FILE_NAME, false, 0, false);
		LogReplayer replayer = new LogReplayer(REPLAY_LOG_FILE_NAME, io);
		try {
			replayer.replayLog();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
