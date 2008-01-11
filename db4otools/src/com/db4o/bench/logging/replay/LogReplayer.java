/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench.logging.replay;

import java.io.*;
import java.util.*;

import com.db4o.bench.logging.*;
import com.db4o.bench.logging.replay.commands.*;
import com.db4o.foundation.*;
import com.db4o.io.IoAdapter;


public class LogReplayer {
	
	private String _logFilePath;
	private IoAdapter _io;
	private Set _command;
	private Map _counts;
	
	public LogReplayer(String logFilePath, IoAdapter io, Set commands) {
		_logFilePath = logFilePath;
		_io = io;
		_command = commands;
		_counts = new HashMap();
		Iterator it = commands.iterator();
		while (it.hasNext()) {
			_counts.put(it.next(), new Long(0));
		}
	}
	
	public LogReplayer(String logFilePath, IoAdapter io) {
		this(logFilePath, io, LogConstants.allEntries());
	}
	
	public void setLog(String logFilePath) {
		_logFilePath = logFilePath;
	}
	
	public void replayLog() throws IOException {
		playCommandList(readCommandList());
	}
	
	public List4 readCommandList() throws IOException {
		List4 list = null;
		BufferedReader reader = new BufferedReader(new FileReader(_logFilePath));
		String line = null;
		while ( (line = reader.readLine()) != null ) {
			IoCommand ioCommand = readLine(line);
			if(ioCommand != null){
				list = new List4(list, ioCommand);
			}
		}
		reader.close();
		return list;
	}
	
	public void playCommandList(List4 commandList){
		while(commandList != null){
			IoCommand ioCommand = (IoCommand) commandList._element;
			ioCommand.replay(_io);
			commandList = commandList._next;
		}
	}
	
	private IoCommand readLine(String line) {
		if ( line.startsWith(LogConstants.WRITE_ENTRY) ) {
			if (! _command.contains(LogConstants.WRITE_ENTRY)) {
				return null;
			}
			int length = (int) parameter(LogConstants.WRITE_ENTRY,  line);
			incrementCount(LogConstants.WRITE_ENTRY);
			return new WriteCommand(length);
		}
		if ( line.startsWith(LogConstants.READ_ENTRY) ) {
			if (! _command.contains(LogConstants.READ_ENTRY)) {
				return null;
			}
			int length = (int) parameter(LogConstants.READ_ENTRY,  line);
			incrementCount(LogConstants.READ_ENTRY);
			return new ReadCommand(length);
		}
		if ( line.startsWith(LogConstants.SYNC_ENTRY) ) {
			if (! _command.contains(LogConstants.SYNC_ENTRY)) {
				return null;
			}
			incrementCount(LogConstants.SYNC_ENTRY);
			return new SyncCommand();
		}
		if ( line.startsWith(LogConstants.SEEK_ENTRY) ) {
			if (! _command.contains(LogConstants.SEEK_ENTRY)) {
				return null;
			}
			int address = (int) parameter(LogConstants.READ_ENTRY,  line);
			incrementCount(LogConstants.SEEK_ENTRY);
			return new SeekCommand(address);
		}
		if(line.length() == 0){
			return null;
		}
		throw new IllegalArgumentException("Unknown command in log: " + line);
	}


//	private void replayLine(String line) {
//		if ( line.startsWith(LogConstants.WRITE_ENTRY) ) {
//			if (_command.contains(LogConstants.WRITE_ENTRY)) {
//				replayWrite(line);
//				incrementCount(LogConstants.WRITE_ENTRY);
//			}
//			return;
//		}
//		if ( line.startsWith(LogConstants.READ_ENTRY) ) {
//			if (_command.contains(LogConstants.READ_ENTRY)) {
//				replayRead(line);
//				incrementCount(LogConstants.READ_ENTRY);
//			}
//			return;
//		}
//		if ( line.startsWith(LogConstants.SYNC_ENTRY) ) {
//			if (_command.contains(LogConstants.SYNC_ENTRY)) {
//				replaySync();
//				incrementCount(LogConstants.SYNC_ENTRY);
//			}
//			return;
//		}
//		if ( line.startsWith(LogConstants.SEEK_ENTRY) ) {
//			if (_command.contains(LogConstants.SEEK_ENTRY)) {
//				replaySeek(line);
//				incrementCount(LogConstants.SEEK_ENTRY);
//			}
//			return;
//		}
//		if(line.length() == 0){
//			return;
//		}
//		throw new IllegalArgumentException("Unknown command in log: " + line);
//	}


//	private void replaySync() {
//		_io.sync();
//	}
//	
//	private void replayRead(String line) {
//		byte[] buffer = prepareBuffer(LogConstants.READ_ENTRY, line);
//		_io.read(buffer, buffer.length);
//	}
//
//	private void replayWrite(String line) {
//		byte[] buffer = prepareBuffer(LogConstants.WRITE_ENTRY, line);		
//		_io.write(buffer);
//	}
//	
//	private void replaySeek(String line) {
//		long pos = parameter(LogConstants.SEEK_ENTRY.length(), line);
//		_io.seek(pos);
//	}

//	private byte[] prepareBuffer(String command, String line) {
//		int length = (int) parameter(command.length(),  line);
//		return new byte[length];
//	}
	
	private long parameter(String command, String line){
		return parameter(command.length(),  line);
	}

	private long parameter(int start, String line) {
		return Long.parseLong(line.substring(start));
	}
	
	private void incrementCount(String key) {
		long count = ((Long)_counts.get(key)).longValue();
		_counts.put(key, new Long(count+1));
	}
	
	public Map operationCounts() {
		return _counts;
	}
}
