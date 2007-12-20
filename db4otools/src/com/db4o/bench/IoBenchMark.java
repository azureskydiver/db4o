/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.bench;

import java.io.*;
import java.util.*;

import com.db4o.bench.crud.*;
import com.db4o.bench.logging.*;
import com.db4o.bench.logging.replay.*;
import com.db4o.io.*;

public class IoBenchMark {
	
	private static final int ITERATIONS = 1;	//100
	
	private static final int SMALL 	= 10;		//1'000
	
	private static final int MEDIUM	= 30000;	//30'000
	
	private static final int LARGE 	= 1000000;	//1'000'000
	
	private static final String DB_FILE_NAME = "ioBenchMark.db4o";
	
	public static void main(String[] args) throws IOException {
		
		printDoubleLine();
		System.out.println("Running db4o IoBenchMark");
		System.out.println("Iterations on each operation: " + ITERATIONS);
		printDoubleLine();

		IoBenchMark ioBenchMark = new IoBenchMark();
		ioBenchMark.run(SMALL, ITERATIONS);
//		ioBenchMark.run(MEDIUM, ITERATIONS);
//		ioBenchMark.run(LARGE, ITERATIONS);
	}

	private void run(int itemCount, int iterations) throws FileNotFoundException, IOException {
		
		new CrudApplication().run(itemCount);
		
		IoAdapter rafFactory = new RandomAccessFileAdapter();
		IoAdapter raf = rafFactory.open(DB_FILE_NAME, false, 0, false);
		LogReplayer replayer = new LogReplayer(CrudApplication.logFileName(itemCount), raf);
		replayer.replayLog();
		
		new File(logFileName(itemCount)).delete();
		
		for (int i = 0; i < LogConstants.ALL_ENTRIES.length; i++) {
			HashSet commands = new HashSet();
			commands.add(LogConstants.ALL_ENTRIES[i]);
			
			rafFactory = new RandomAccessFileAdapter();
			IoAdapter bmFactory = new BenchmarkIoAdapter(rafFactory, logFileName(itemCount), iterations, true);
			BenchmarkIoAdapter io = (BenchmarkIoAdapter) bmFactory.open(DB_FILE_NAME, false, 0, false);
			replayer = new LogReplayer(CrudApplication.logFileName(itemCount), io, commands);
			replayer.replayLog();
		}
		
		new File(DB_FILE_NAME).delete();
		
		printSingleLine();
		System.out.println("IoBenchmark results with " + itemCount + " items");
		System.out.println("Statistics written to " + logFileName(itemCount));
		printSingleLine();
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(logFileName(itemCount))));
		String line = null;
		while ( (line = reader.readLine()) != null ) {
			System.out.println(line);
		}
	}
	
	private static String logFileName(int itemCount){
		return "db4o-io-benchmark-results-" + itemCount + ".log";
	}
	
	private static void printSingleLine() {
		System.out.println("-------------------------------------------------------------");
	}
	
	private static void printDoubleLine() {
		System.out.println("=============================================================");
	}
	
	

}
