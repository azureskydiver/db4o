/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench.delaying;

import java.io.*;

import com.db4o.bench.logging.*;


public class DelayCalculation {

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
	
	public Delays getDelays() {
		long writeDelay, readDelay, seekDelay, syncDelay;
		String units = null;
		
		double writeDifference = _slowerMachine.writeTime() - _fasterMachine.writeTime();
		double readDifference  = _slowerMachine.readTime()  - _fasterMachine.readTime();
		double seekDifference  = _slowerMachine.seekTime()  - _fasterMachine.seekTime();
		double snycDifference  = _slowerMachine.syncTime()  - _fasterMachine.syncTime();
		
		long millisecond = (long)Math.pow(10, 6);	// 1ms in nanoseconds
		if ( (writeDifference < millisecond) || (readDifference < millisecond) || (seekDifference < millisecond) || (snycDifference < millisecond) ) {
			writeDelay = (long)writeDifference;
			readDelay  = (long)readDifference;
			seekDelay  = (long)seekDifference;
			syncDelay  = (long)snycDifference;
			units = Delays.UNITS_NANOSECONDS;
		}
		else {	
			writeDelay = (long)writeDifference / millisecond;
			readDelay  = (long)readDifference / millisecond;
			seekDelay  = (long)seekDifference / millisecond;
			syncDelay  = (long)snycDifference / millisecond;
			units = Delays.UNITS_MILLISECONDS;
		}
		
		Delays delays = new Delays(readDelay, writeDelay, seekDelay, syncDelay, units);
		return delays;
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
				String[] parts = line.split(" ");
				_readTime = Double.parseDouble(parts[1]);
			}
			else if (line.startsWith(LogConstants.WRITE_ENTRY)) {
				String[] parts = line.split(" ");
				_writeTime = Double.parseDouble(parts[1]);
			}
			else if (line.startsWith(LogConstants.SEEK_ENTRY)) {
				String[] parts = line.split(" ");
				_seekTime = Double.parseDouble(parts[1]);
			}
			else if (line.startsWith(LogConstants.SYNC_ENTRY)) {
				String[] parts = line.split(" ");
				_syncTime = Double.parseDouble(parts[1]);
			}
		}
		reader.close();
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