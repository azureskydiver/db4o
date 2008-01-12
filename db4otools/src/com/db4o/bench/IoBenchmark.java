/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.bench;

import java.io.*;
import java.util.*;

import com.db4o.bench.crud.*;
import com.db4o.bench.delaying.*;
import com.db4o.bench.logging.*;
import com.db4o.bench.logging.replay.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.io.*;

public class IoBenchmark {
	
	
	private static final String _doubleLine = "=============================================================";

	private static final String _singleLine = "-------------------------------------------------------------";
	
	private Delays _delays = null;
	
	
	public static void main(String[] args) throws IOException {
		if (args.length != 2 && args.length != 4) {
			System.out.println("Usage: IoBenchmark <object-count> <db-file-name> [<results-file-1> <results-file-2>]");
			System.exit(1);
		}		
		printBenchmarkHeader();
		IoBenchmark ioBenchmark = new IoBenchmark();
		if (args.length == 2 || (args[2] == "" && args[3] == "")) {
			ioBenchmark.run(Integer.parseInt(args[0]), args[1]);
		}
		else {
			ioBenchmark.runDelayed(Integer.parseInt(args[0]), args[1], args[2], args[3]);
		}	
	}

	
	private void run(int itemCount, String dbFileName) throws IOException {
		runTargetApplication(itemCount);
		prepareDbFile(itemCount, dbFileName);
		runBenchmark(itemCount, dbFileName);
	}

	
	private void runDelayed(int itemCount, String dbFileName, String resultsFile1, String resultsFile2) throws IOException {
		processResultsFiles(resultsFile1, resultsFile2);;
		run(itemCount, dbFileName);
	}

	
	private void runTargetApplication(int itemCount) {
		System.out.println("Running target application ...");
		new CrudApplication().run(itemCount);
	}
	

	private void prepareDbFile(int itemCount, String dbFileName) throws IOException {
		System.out.println("Preparing DB file ...");
		removeDbFile(dbFileName);
		IoAdapter rafFactory = new RandomAccessFileAdapter();
		IoAdapter raf = rafFactory.open(dbFileName, false, 0, false);
		LogReplayer replayer = new LogReplayer(CrudApplication.logFileName(itemCount), raf);
		replayer.replayLog();		
		raf.close();
	}


	private void runBenchmark(int itemCount, String dbFileName) throws IOException {
		System.out.println("Running benchmark ...");
		removeExistingLog(itemCount);
		PrintStream out = new PrintStream(new FileOutputStream(logFileName(itemCount), true));
		printRunHeader(itemCount, out);
		for (int i = 0; i < LogConstants.ALL_ENTRIES.length; i++) {
			String currentCommand = LogConstants.ALL_ENTRIES[i];
			benchmarkCommand(currentCommand, itemCount, dbFileName, out);	
		}
		removeDbFile(dbFileName);
	}

		
	private void benchmarkCommand(String command, int itemCount, String dbFileName, PrintStream out) throws IOException {
		HashSet commands = commandSet(command);
		IoAdapter io = ioAdapter(dbFileName);
		LogReplayer replayer = new LogReplayer(CrudApplication.logFileName(itemCount), io, commands);
		List4 commandList = replayer.readCommandList();
		
		StopWatch watch = new StopWatch();
		watch.start();
		replayer.playCommandList(commandList);		
		watch.stop();
		io.close();
		
		long timeElapsed = watch.elapsed();
		long operationCount = ((Long)replayer.operationCounts().get(command)).longValue();
		printStatisticsForCommand(out, command, timeElapsed, operationCount);
	}


	private IoAdapter ioAdapter(String dbFileName) throws NumberFormatException, IOException, Db4oIOException {
		if (delayed()) {
			return delayingIoAdapter(dbFileName);
		}
		
		IoAdapter rafFactory = new RandomAccessFileAdapter();
		return rafFactory.open(dbFileName, false, 0, false);
	}
	
	
	private IoAdapter delayingIoAdapter(String dbFileName) throws NumberFormatException, IOException {
		IoAdapter rafFactory = new RandomAccessFileAdapter();
		IoAdapter delFactory = new DelayingIoAdapter(rafFactory, _delays);
		return delFactory.open(dbFileName, false, 0, false);
	}


	private void processResultsFiles(String resultsFile1, String resultsFile2) throws NumberFormatException, IOException {
		System.out.println("Delaying:");
		System.out.print("> ");
		DelayCalculation calculation = new DelayCalculation(resultsFile1, resultsFile2);
		calculation.validateData();
		if (calculation.isValidData()) {
			_delays = calculation.getDelays();
			System.out.println("> " + _delays);
			if ( _delays.units == Delays.UNITS_MILLISECONDS ) {
				System.out.println("> Delaying with Thread.sleep() ...");
			}
			else if ( _delays.units == Delays.UNITS_NANOSECONDS ) {
				System.out.println("> Delaying with busy waiting ...");
			}
		}
		else {
			System.err.println("> Results file are invalid for delaying!");
			System.err.println("> Aborting execution!");
			System.exit(1);
		}
	}
	
	private String logFileName(int itemCount){
		String fileName =  "db4o-IoBenchmark-results-" + itemCount;
		if (delayed()) {
			fileName += "-delayed";
		}
		fileName += ".log";
		return fileName;
	}

	private boolean delayed() {
		return _delays != null;
	}
	
	private HashSet commandSet(String command) {
		HashSet commands = new HashSet();
		commands.add(command);
		return commands;
	}
	
	private void removeExistingLog(int itemCount) {
		new File(logFileName(itemCount)).delete();
	}
	
	private void removeDbFile(String dbFileName) {
		new File(dbFileName).delete();
	}
	
	private static void printBenchmarkHeader() {
		printDoubleLine();
		System.out.println("Running db4o IoBenchMark");
		printDoubleLine();
	}
	
	private void printRunHeader(int itemCount, PrintStream out) {
		output(out, _singleLine);
		output(out, "db4o IoBenchmark results with " + itemCount + " items");
		System.out.println("Statistics written to " + logFileName(itemCount));
		output(out, _singleLine);
		output(out, "");
	}
	
	private void printStatisticsForCommand(PrintStream out, String currentCommand, long timeElapsed, long operationCount) {
		double avgTimePerOp = (double)timeElapsed/(double)operationCount;
		double opsPerMs = (double)operationCount/(double)timeElapsed;
		double nanosPerMilli = Math.pow(10, 6);
		
		String output = "Results for " + currentCommand + "\r\n" +
						"> time elapsed: " + timeElapsed + " ms" + "\r\n" + 
						"> operations executed: " + operationCount + "\r\n" +
						"> operations per millisecond: " + opsPerMs + "\r\n" +
						"> average duration per operation: " + avgTimePerOp + " ms" + "\r\n" +
						currentCommand + (avgTimePerOp*nanosPerMilli) + " (I/O Benchmark value. Smaller numbers are better)\r\n";
		
		output(out, output);
	}

	private void output(PrintStream out, String text) {
		out.println(text);
		System.out.println(text);
	}
	
	
	private static void printDoubleLine() {
		System.out.println(_doubleLine);
	}
	
}
