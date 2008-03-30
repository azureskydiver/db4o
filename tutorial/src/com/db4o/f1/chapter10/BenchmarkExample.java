/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.f1.chapter10;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;

import com.db4o.bench.delaying.DelayCalculation;
import com.db4o.bench.delaying.DelayingIoAdapter;
import com.db4o.bench.delaying.Delays;
import com.db4o.bench.logging.LogConstants;
import com.db4o.bench.logging.LoggingIoAdapter;
import com.db4o.bench.logging.replay.LogReplayer;
import com.db4o.ext.Db4oIOException;
import com.db4o.foundation.List4;
import com.db4o.foundation.StopWatch;
import com.db4o.io.IoAdapter;
import com.db4o.io.RandomAccessFileAdapter;

public class BenchmarkExample {
	
	private static final int _objectCount = 10;
	
	private static final String _resultsFile2 = "db4o-IoBenchmark-results-10-slower.log";
	private static final String _resultsFile1 = "db4o-IoBenchmark-results-10.log";
	
	private static final String _doubleLine = "=============================================================";

	private static final String _singleLine = "-------------------------------------------------------------";
	
	private static final String _dbFileName = "IoBenchmark.db4o";
	
	private static Delays _delays = null;
	
	
	public static void main(String[] args) throws IOException {
		runNormal();
		runDelayed();
	}

	public static void runNormal() throws IOException{
		printDoubleLine();
    	RandomAccessFileAdapter rafAdapter = new RandomAccessFileAdapter();
		IoAdapter ioAdapter = new LoggingIoAdapter(rafAdapter, "test.log");
        		
		System.out.println("Running db4o IoBenchmark");
		printDoubleLine();
		// Run a target application and log its I/O access pattern
		runTargetApplication(_objectCount);
		// Replay the recorded I/O operations once to prepare a database file.
		prepareDbFile(_objectCount);
		// Replay the recorded I/O operations a second time. 
		// Operations are grouped by command type (read, write, seek, sync), 
		// and the total time executing all operations of a specific command type is measured. 
		runBenchmark(_objectCount);
	}

	public static void runDelayed() throws IOException {
		printDoubleLine();
		System.out.println("Running db4o IoBenchmark");
		printDoubleLine();
		// Write sample slow data to the test file
		prepareDelayedFile(_resultsFile2);
		// calculate the delays to match the slower device
		processResultsFiles(_resultsFile1, _resultsFile2);
		// Run a target application and log its I/O access pattern
		runTargetApplication(_objectCount);
		// Replay the recorded I/O operations once to prepare a database file.
		prepareDbFile(_objectCount);
		// Replay the recorded I/O operations a second time. 
		// Operations are grouped by command type (read, write, seek, sync), 
		// and the total time executing all operations of a specific command type is measured. 
		runBenchmark(_objectCount);
	}
		
	public static void prepareDelayedFile(String fileName) {
		try {
			PrintStream out = new PrintStream(new FileOutputStream(fileName));
			out.print("-------------------------------------------------------------\r\n" + 
					"db4o IoBenchmark results with 10 items\r\n" + 
					"-------------------------------------------------------------\r\n" + 
					"\r\n" + 
					"Results for READ \r\n" + 
					"> operations executed: 91\r\n" + 
					"> time elapsed: 4 ms\r\n" + 
					"> operations per millisecond: 22.7\r\n" + 
					"> average duration per operation: 0.043956043956043956 ms\r\n" + 
					"READ 43956 ns\r\n" + 
					"\r\n" + 
					"Results for WRITE \r\n" + 
					"> operations executed: 85\r\n" + 
					"> time elapsed: 4 ms\r\n" + 
					"> operations per millisecond: 21.2\r\n" + 
					"> average duration per operation: 0.047058823529411764 ms\r\n" + 
					"WRITE 47058 ns\r\n" + 
					"\r\n" + 
					"Results for SYNC \r\n" + 
					"> operations executed: 45\r\n" + 
					"> time elapsed: 98 ms\r\n" + 
					"> operations per millisecond: 0.4591836734693877\r\n" + 
					"> average duration per operation: 2.177777777777777777 ms\r\n" + 
					"SYNC 2177776 ns\r\n" + 
					"\r\n" + 
					"Results for SEEK \r\n" + 
					"> operations executed: 176\r\n" + 
					"> time elapsed: 2 ms\r\n" + 
					"> operations per millisecond: 88.0\r\n" + 
					"> average duration per operation: 0.011363636363636363 ms\r\n" + 
					"SEEK 11362 ns\r\n");
			out.close();
		} catch (FileNotFoundException ex){
			throw new RuntimeException(ex);
		}
	}

	public static void runTargetApplication(int itemCount) {
		// Stage 1: running the application to record IO access 
		System.out.println("Running target application ...");
		// Any custom application can be used instead
		new CrudApplication().run(itemCount);
	}


	public static void prepareDbFile(int itemCount) {
		// Stage 2:Replay the recorded IO to prepare a database file
		System.out.println("Preparing DB file ...");
		deleteFile(_dbFileName);
		IoAdapter rafFactory = new RandomAccessFileAdapter();
		IoAdapter raf = rafFactory.open(_dbFileName, false, 0, false);
		// Use the file with the recorded operations from stage 1
		LogReplayer replayer = new LogReplayer("simplecrud_" + itemCount + ".log", raf);
		try {
			replayer.replayLog();
		} catch (IOException e) {
			exitWithError("Error reading I/O operations log file");
		} finally {
			raf.close();
		}
	}


	public static void runBenchmark(int itemCount) throws IOException {
		// Stage 3: Replay the recorded IO grouping command types
		System.out.println("Running benchmark ...");
		deleteBenchmarkResultsFile(itemCount);
		PrintStream out = new PrintStream(new FileOutputStream(resultsFileName(itemCount), true));
		printRunHeader(itemCount, out);
		// run all commands: READ_ENTRY, WRITE_ENTRY, SYNC_ENTRY, SEEK_ENTRY
		for (int i = 0; i < LogConstants.ALL_CONSTANTS.length; i++) {
			String currentCommand = LogConstants.ALL_CONSTANTS[i];
			benchmarkCommand(currentCommand, itemCount, _dbFileName, out);	
		}
		out.close();
		deleteFile(_dbFileName);
		deleteCrudLogFile(itemCount);
	}

		
	public static void benchmarkCommand(String command, int itemCount, String dbFileName, PrintStream out) throws IOException {
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


	public static IoAdapter ioAdapter(String dbFileName) throws NumberFormatException, IOException, Db4oIOException {
		if (delayed()) {
			return delayingIoAdapter(dbFileName);
		}
		
		IoAdapter rafFactory = new RandomAccessFileAdapter();
		return rafFactory.open(dbFileName, false, 0, false);
	}
	
	
	public static IoAdapter delayingIoAdapter(String dbFileName) throws NumberFormatException{
		IoAdapter rafFactory = new RandomAccessFileAdapter();
		IoAdapter delFactory = new DelayingIoAdapter(rafFactory, _delays);
		return delFactory.open(dbFileName, false, 0, false);
	}


	public static void processResultsFiles(String resultsFile1, String resultsFile2) throws NumberFormatException {
		System.out.println("Delaying:");
		try {
			DelayCalculation calculation = new DelayCalculation(resultsFile1, resultsFile2);
			calculation.validateData();
			if (!calculation.isValidData()) {
				exitWithError("> Result files are invalid for delaying!");
			}
			_delays = calculation.calculatedDelays();
			System.out.println("> Required delays:");
			System.out.println("> " + _delays);
            System.out.println("> Adjusting delay timer to match required delays...");
			calculation.adjustDelays(_delays);
			System.out.println("> Adjusted delays:");
			System.out.println("> " + _delays);
		} catch (IOException e) {
			exitWithError("> Could not open results file(s)!\n" +
						"> Please check the file name settings in IoBenchmark.properties.");
		}
	}


	public static void exitWithError(String error) {
		System.err.println(error + "\n Aborting execution!");
		throw new RuntimeException(error + "\n Aborting execution!");
	}
	
	public static String resultsFileName(int itemCount){
		String fileName =  "db4o-IoBenchmark-results-" + itemCount;
		if (delayed()) {
			fileName += "-delayed";
		}
		fileName += ".log";
		return fileName;
	}

	public static boolean delayed() {
		return _delays != null;
	}
	
	public static HashSet commandSet(String command) {
		HashSet commands = new HashSet();
		commands.add(command);
		return commands;
	}
	
	public static void deleteBenchmarkResultsFile(int itemCount) {
		deleteFile(resultsFileName(itemCount));
	}
	
	public static void deleteCrudLogFile(int itemCount) {
		deleteFile("simplecrud_" + itemCount + ".log");
	}

	public static void deleteFile(String fileName) {
		new File(fileName).delete();
	}

	
	public static void printRunHeader(int itemCount, PrintStream out) {
		output(out, _singleLine);
		output(out, "db4o IoBenchmark results with " + itemCount + " items");
		System.out.println("Statistics written to " + resultsFileName(itemCount));
		output(out, _singleLine);
		output(out, "");
	}
	
	public static void printStatisticsForCommand(PrintStream out, String currentCommand, long timeElapsed, long operationCount) {
		double avgTimePerOp = (double)timeElapsed/(double)operationCount;
		double opsPerMs = (double)operationCount/(double)timeElapsed;
		double nanosPerMilli = Math.pow(10, 6);
		
		String output = "Results for " + currentCommand + "\r\n" +
						"> operations executed: " + operationCount + "\r\n" +
						"> time elapsed: " + timeElapsed + " ms\r\n" + 
						"> operations per millisecond: " + opsPerMs + "\r\n" +
						"> average duration per operation: " + avgTimePerOp + " ms\r\n" +
						currentCommand + (int)(avgTimePerOp*nanosPerMilli) + " ns\r\n";
		
		output(out, output);
		System.out.println(" ");
	}

	public static void output(PrintStream out, String text) {
		out.println(text);
		System.out.println(text);
	}
	
	
	public static void printDoubleLine() {
		System.out.println(_doubleLine);
	}
	

}
