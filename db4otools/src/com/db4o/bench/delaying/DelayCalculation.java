/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench.delaying;

import java.io.*;

import com.db4o.bench.logging.*;
import com.db4o.bench.timing.*;


public class DelayCalculation {

	private static final int ADJUSTMENT_ITERATIONS = 10000;
	
	private MachineCharacteristics _machine1;
	private MachineCharacteristics _machine2;
	private MachineCharacteristics _fasterMachine = null;
	private MachineCharacteristics _slowerMachine = null;
	
	public DelayCalculation(String logFileName1, String logFileName2) throws NumberFormatException, IOException {
		_machine1 = new MachineCharacteristics(logFileName1);
		_machine2 = new MachineCharacteristics(logFileName2);
	}
	
	public void validateData() {
		if (
				(_machine1.writeTime() <= _machine2.writeTime()) &&
				(_machine1.readTime()  <= _machine2.readTime() ) &&
				(_machine1.seekTime()  <= _machine2.seekTime() ) &&
				(_machine1.syncTime()  <= _machine2.syncTime() )
			) 
		{
			_fasterMachine = _machine1;
			_slowerMachine = _machine2;
			System.out.println("machine1 ("+ _machine1.logFileName() +") is faster!");
		}
			
		else if (
				(_machine1.writeTime() >= _machine2.writeTime()) &&
				(_machine1.readTime()  >= _machine2.readTime() ) &&
				(_machine1.seekTime()  >= _machine2.seekTime() ) &&
				(_machine1.syncTime()  >= _machine2.syncTime() )
			)
		{
			_fasterMachine = _machine2;
			_slowerMachine = _machine1;
			System.out.println("machine2 ("+ _machine2.logFileName() +") is faster!");
		}
	}
	
	public boolean isValidData() {
		return ((_fasterMachine != null) && (_slowerMachine != null));
	}
	

	public Delays calculatedDelays() {
		long writeDelay, readDelay, seekDelay, syncDelay;
		String units = null;
		
		double writeDifference = _slowerMachine.writeTime() - _fasterMachine.writeTime();
		double readDifference  = _slowerMachine.readTime()  - _fasterMachine.readTime();
		double seekDifference  = _slowerMachine.seekTime()  - _fasterMachine.seekTime();
		double snycDifference  = _slowerMachine.syncTime()  - _fasterMachine.syncTime();
		
		long nanosPerMilli = (long)Math.pow(10, 6);
		if ( (writeDifference < nanosPerMilli) || (readDifference < nanosPerMilli) || (seekDifference < nanosPerMilli) || (snycDifference < nanosPerMilli) ) {
			writeDelay = (long)writeDifference;
			readDelay  = (long)readDifference;
			seekDelay  = (long)seekDifference;
			syncDelay  = (long)snycDifference;
			units = Delays.UNITS_NANOSECONDS;
		}
		else {	
			writeDelay = (long)writeDifference / nanosPerMilli;
			readDelay  = (long)readDifference / nanosPerMilli;
			seekDelay  = (long)seekDifference / nanosPerMilli;
			syncDelay  = (long)snycDifference / nanosPerMilli;
			units = Delays.UNITS_MILLISECONDS;
		}
		
		Delays delays = new Delays(readDelay, writeDelay, seekDelay, syncDelay, units);
		return delays;
	}
	
	public Delays adjustNanoDelays(Delays delays) throws InvalidDelayException {
		long readDelay = adjustNanoDelay(delays.readDelay);
		long writeDelay = adjustNanoDelay(delays.writeDelay);
		long seekDelay = adjustNanoDelay(delays.seekDelay);
		long syncDelay = adjustNanoDelay(delays.syncDelay);
		return new Delays(readDelay, writeDelay, seekDelay, syncDelay, delays.units);
	}
	

	private long adjustNanoDelay(long delay) throws InvalidDelayException {
		NanoStopWatch watch = new NanoStopWatch();
		NanoTiming timing = new NanoTiming();
		long difference, differencePerIteration;
		long average = 0, oldAverage = 0;
		long adjustedDelay = delay;
		int adjustmentRuns = 1;
		long targetRuntime = ADJUSTMENT_ITERATIONS*delay;
        long minimumDelay = minimumDelay();
        warmUpIterations(delay, timing);	
        
        do {
        	watch.start();
        	for (int i = 0; i < ADJUSTMENT_ITERATIONS; i++) {
        		timing.waitNano(adjustedDelay);
        	}
        	watch.stop();
        	
        	difference = targetRuntime - watch.elapsed();
        	differencePerIteration = difference/ADJUSTMENT_ITERATIONS;
        	if (-differencePerIteration > adjustedDelay) {
        		adjustedDelay /= 2;				
        	} else {
        		/**
        		 * TODO: Which version to use ???
        		 * adjustedDelay += differencePerIteration
        		 * adjustedDelay = delay + differencePerIteration; 
        		 */
        		adjustedDelay += differencePerIteration;
        		
        		oldAverage = average;
        		if (adjustmentRuns == 1) {
        			average = adjustedDelay;
        		}
        		else {
        			average = ((average*(adjustmentRuns-1)) / adjustmentRuns) + (adjustedDelay / adjustmentRuns);
        		}
        		adjustmentRuns++;
        	}
        	if(adjustedDelay <= 0){
        	    break;
        	}
        	if( (Math.abs(average - oldAverage) < (0.01*delay)) && adjustmentRuns > 100){
        	    break;
        	}
        } while (true);
        if (average < minimumDelay) {
            System.err.println("Smallest achievable delay: " + minimumDelay);
            System.err.println("Required delay setting: " + average);
            System.err.println("Using delay(0) to wait as short as possible.");
            System.err.println("Results will not be accurate.");
            return 0;
        }
		return average;
	}

	private void warmUpIterations(long delay, NanoTiming timing) {
		for (int i = 0; i < ADJUSTMENT_ITERATIONS; i++) {
			timing.waitNano(delay);
		}
	}
	
	private long minimumDelay() {
		NanoStopWatch watch = new NanoStopWatch();
		NanoTiming timing = new NanoTiming();
		watch.start();
		for (int i = 0; i < ADJUSTMENT_ITERATIONS; i++) {
			timing.waitNano(0);
		}
		watch.stop();
		return watch.elapsed()/ADJUSTMENT_ITERATIONS;
	}
}


class MachineCharacteristics {

	private String _logFileName;
	private double _writeTime;
	private double _readTime;
	private double _syncTime;
	private double _seekTime;

	public MachineCharacteristics(String logFileName) throws NumberFormatException, IOException {
		_logFileName = logFileName;
		parseLog();
	}

	private void parseLog() throws NumberFormatException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(_logFileName));
		String line = null;
		while ( (line = reader.readLine()) != null ) {
			if (line.startsWith(LogConstants.READ_ENTRY)) {
				_readTime = Double.parseDouble(extractNumber(line));
			}
			else if (line.startsWith(LogConstants.WRITE_ENTRY)) {
				_writeTime = Double.parseDouble(extractNumber(line));
			}
			else if (line.startsWith(LogConstants.SEEK_ENTRY)) {
				_seekTime = Double.parseDouble(extractNumber(line));
			}
			else if (line.startsWith(LogConstants.SYNC_ENTRY)) {
				_syncTime = Double.parseDouble(extractNumber(line));
			}
		}
		reader.close();
	}

	private String extractNumber(String line) {
		int start = line.indexOf(' ');
		int end = line.indexOf(' ', start+1);
		return line.substring(start, end);
	}
	
	public String logFileName() {
		return _logFileName;
	}
	
	public double writeTime() {
		return _writeTime;
	}
	
	public double readTime() {
		return _readTime;
	}
	
	public double seekTime() {
		return _seekTime;
	}
	
	public double syncTime() {
		return _syncTime;
	}
}