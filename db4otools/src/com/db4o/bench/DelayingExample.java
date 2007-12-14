/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench;

import com.db4o.*;
import com.db4o.io.*;


public class DelayingExample {

	private static final String _logFileName1 = "slower-nanos.log";
	private static final String _logFileName2 = "z3raDesk2_polepos-allopt-cs.log";
	private static final String DB_FILE_NAME = "delay-test.db4o";
	private static final String OPEN_LOG_FILE_NAME = "rssowl_open-close.log";
	private static final String BENCHMARK_LOG_FILE_NAME = "DelayingExample-benchmarkTest.log";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new DelayingExample();
	}

	public DelayingExample() {
		DelayCalculation calculation = new DelayCalculation(_logFileName1, _logFileName2);
		calculation.validateData();
		if (calculation.isValidData()) {
			System.out.println("Data is valid!");
			Delays delays = calculation.getDelays();
			System.out.println("delays: " + delays);
			
			dbTest(delays);
//			replayTest(delays);
//			benchmarkTestRAF(delays);
		}
		else {
			System.err.println("Data is not valid!");
		}
	}

	private void dbTest(Delays delays) {
		IoAdapter rafFactory = new RandomAccessFileAdapter();
		IoAdapter delFactory = new DelayingIoAdapter(rafFactory, delays);
		IoAdapter io = delFactory.open(DB_FILE_NAME, false, 0, false);
		Db4o.configure().io(io);
		ObjectContainer db = Db4o.openFile(DB_FILE_NAME);
		db.close();
	}
	
	private void replayTest(Delays delays) {
		IoAdapter rafFactory = new RandomAccessFileAdapter();
		IoAdapter delFactory = new DelayingIoAdapter(rafFactory, delays);
		IoAdapter io = delFactory.open(DB_FILE_NAME, false, 0, false);
		LogReplayer replayer = new LogReplayer(OPEN_LOG_FILE_NAME, io);
		replayer.replayLog();
	}
	
	private void benchmarkTestRAF(Delays delays) {
		IoAdapter rafFactory = new RandomAccessFileAdapter();
		IoAdapter delFactory = new DelayingIoAdapter(rafFactory, delays);
		IoAdapter bmFactory = new BenchmarkIoAdapter(delFactory, BENCHMARK_LOG_FILE_NAME, 1);
		IoAdapter io = bmFactory.open(DB_FILE_NAME, false, 0, false);
		LogReplayer replayer = new LogReplayer(OPEN_LOG_FILE_NAME, io);
		replayer.replayLog();
	}
	
}
