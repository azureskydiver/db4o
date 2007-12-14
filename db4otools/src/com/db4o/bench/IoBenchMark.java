/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.bench;

import java.io.*;

import com.db4o.bench.crud.*;
import com.db4o.io.*;

public class IoBenchMark {
	
	private static final int ITERATIONS = 100;
	
	private static final int SMALL = 10;
	
	private static final int MEDIUM = 3000;
	
	private static final int LARGE = 10000;
	
	private static final String DB_FILE_NAME = "ioBenchMark.db4o";
	
	public static void main(String[] args) throws IOException {
		
		printLine();
		System.out.println("Running db4o IoBenchMark");
		System.out.println("Iterations on each operation: " + ITERATIONS);
		printLine();

		IoBenchMark ioBenchMark = new IoBenchMark();
		ioBenchMark.run(SMALL, ITERATIONS);
		ioBenchMark.run(MEDIUM, ITERATIONS);
		ioBenchMark.run(LARGE, ITERATIONS);
	}

	private void run(int itemCount, int iterations) throws FileNotFoundException, IOException {
		
		new CrudApplication().run(itemCount);
		
		IoAdapter rafFactory = new RandomAccessFileAdapter();
		IoAdapter bmFactory = new BenchmarkIoAdapter(rafFactory, logFileName(itemCount), iterations);
		BenchmarkIoAdapter io = (BenchmarkIoAdapter) bmFactory.open(DB_FILE_NAME, false, 0, false);
		LogReplayer replayer = new LogReplayer(CrudApplication.logFileName(itemCount), io);
		replayer.replayLog();
		
		System.out.println("\nIoBenchmark results with " + itemCount + " items:\n");
		System.out.println("Statistics written to " + logFileName(itemCount));
		printLine();
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(logFileName(itemCount))));
		String line = null;
		while ( (line = reader.readLine()) != null ) {
			System.out.println(line);
		}
	}
	
	private static String logFileName(int itemCount){
		return "db4o-io-benchmark-results-" + itemCount + ".log";
	}
	
	private static void printLine() {
		System.out.println("---------------------------------------------------");
	}
	
	

}
