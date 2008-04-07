/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.bench;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;

import com.db4o.Db4oIOException;
import com.db4o.bench.logging.LogConstants;
import com.db4o.bench.logging.replay.LogReplayer;
import com.db4o.foundation.List4;
import com.db4o.foundation.StopWatch;
import com.db4o.io.IoAdapter;
import com.db4o.io.RandomAccessFileAdapter;


/** 
 * IoBenchmark is a benchmark that measures I/O performance as seen by db4o.
 * The benchmark can also run in delayed mode which allows simulating the I/O behaviour of a slower machine
 * on a faster one.
 * 
 * For further information and usage instructions please refer to README.htm.
 * @sharpen.ignore
 */

public class IoBenchmark {
	
	
	private static final String _doubleLine = "=============================================================";

	private static final String _singleLine = "-------------------------------------------------------------";
	
	private static final String _dbFileName = "IoBenchmark.db4o";
	
	
	private final static int REQUIRED_ARGS_COUNT = 3;
	
	public static void main(String[] args) throws Exception {
		if(args.length < 3) {
			System.err.println("Usage: java IoBenchmark <target app class name> <log file path> <result file path> [<target app args>]");
		}
		String targetAppClassName = args[0];
		String logFilePath = args[1];
		String resultFilePath = args[2];
		String[] appArgs = new String[args.length - REQUIRED_ARGS_COUNT];
		System.arraycopy(args, REQUIRED_ARGS_COUNT, appArgs, 0, appArgs.length);
		TargetApplication targetApp = (TargetApplication) Class.forName(targetAppClassName).newInstance();
		printBenchmarkHeader();
		new IoBenchmark().run(targetApp, logFilePath, resultFilePath, appArgs);
	}

	
	private void run(TargetApplication targetApp, String logFilePath, String resultFilePath, String[] appArgs) throws IOException {
		deleteFile(logFilePath);
		deleteFile(resultFilePath);
		runTargetApplication(targetApp, logFilePath, appArgs);
		prepareDbFile(logFilePath);
		runBenchmark(logFilePath, resultFilePath);
	}


	
	private void runTargetApplication(TargetApplication targetApp, String logFilePath, String[] appArgs) {
		sysout("Running target application " + targetApp.getClass().getName() + " with log file " + logFilePath + "...");
		targetApp.run(logFilePath, appArgs);
	}


	private void prepareDbFile(String logFilePath) {
		sysout("Preparing DB file ...");
		deleteFile(_dbFileName);
		IoAdapter rafFactory = new RandomAccessFileAdapter();
		IoAdapter raf = rafFactory.open(_dbFileName, false, 0, false);
		LogReplayer replayer = new LogReplayer(logFilePath, raf);
		try {
			replayer.replayLog();
		} catch (IOException e) {
			exitWithError("Error reading I/O operations log file");
		} finally {
			raf.close();
		}
	}


	private void runBenchmark(String logFilePath, String resultFilePath) throws IOException {
		sysout("Running benchmark ...");
		deleteFile(resultFilePath);
		PrintStream out = new PrintStream(new FileOutputStream(resultFilePath, true));
		printRunHeader(resultFilePath, out);
		for (int i = 0; i < LogConstants.ALL_CONSTANTS.length; i++) {
			String currentCommand = LogConstants.ALL_CONSTANTS[i];
			benchmarkCommand(currentCommand, logFilePath, _dbFileName, out);	
		}
		deleteFile(_dbFileName);
		deleteFile(logFilePath);
	}

		
	private void benchmarkCommand(String command, String logFilePath, String dbFileName, PrintStream out) throws IOException {
		HashSet commands = commandSet(command);
		IoAdapter io = ioAdapter(dbFileName);
		LogReplayer replayer = new LogReplayer(logFilePath, io, commands);
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
		IoAdapter rafFactory = new RandomAccessFileAdapter();
		return rafFactory.open(dbFileName, false, 0, false);
	}
	
	
	private void exitWithError(String error) {
		System.err.println(error + "\n Aborting execution!");
		throw new RuntimeException(error + "\n Aborting execution!");
	}
	
	private HashSet commandSet(String command) {
		HashSet commands = new HashSet();
		commands.add(command);
		return commands;
	}
	
	private void deleteFile(String fileName) {
		new File(fileName).delete();
	}
	
	private static void printBenchmarkHeader() {
		printDoubleLine();
		sysout("Running db4o IoBenchmark");
		printDoubleLine();
	}
	
	private void printRunHeader(String resultFilePath, PrintStream out) {
		output(out, _singleLine);
		output(out, "db4o IoBenchmark results");
		sysout("Statistics written to " + resultFilePath);
		output(out, _singleLine);
		output(out, "");
	}
	
	private void printStatisticsForCommand(PrintStream out, String currentCommand, long timeElapsed, long operationCount) {
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
		sysout(" ");
	}

	private void output(PrintStream out, String text) {
		out.println(text);
		sysout(text);
	}
	
	
	private static void printDoubleLine() {
		sysout(_doubleLine);
	}
	
	private static void sysout(String text) {
		System.out.println(text);
	}
}
