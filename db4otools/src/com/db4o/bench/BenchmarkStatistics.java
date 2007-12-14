/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench;

import java.io.*;
import java.text.*;


public class BenchmarkStatistics {

	private long _byteCount;
	private long _timeCount;
	private long _operationCount;
	private int _iterations;
	
	private String _name;
	
	public BenchmarkStatistics(String name, int iterations) {
		_byteCount = _timeCount = _operationCount = 0;
		_name = name;
		_iterations = iterations;
	}
	
	public void log(long time) {
		_timeCount += time;
		_operationCount++;
	}
	
	public void log(long time, long byteCount) {
		_byteCount += byteCount;
		log(time);
	}
	
	public void printStatistics(PrintStream out) {
		DecimalFormat formatCount = new DecimalFormat("###,###");
		out.println("Statistics for command " + _name);
		out.println("> Number of operations: " + formatCount.format(_operationCount));
		out.println("> Total time taken: " + formatCount.format(_timeCount) + " milliseconds");
		out.println("> Number of bytes handled: " + formatCount.format(_byteCount));
		
		double avgTimePerOp = (double)_timeCount / (double)_operationCount / (double)_iterations;
		out.println(_name + avgTimePerOp + " (I/O Benchmark value, smaller numbers are better)");
		
		out.println();
	}
	
	public void iterations(int i){
		_iterations = i;
	}
}
