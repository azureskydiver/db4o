/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench.logging.replay;

import java.io.*;
import java.util.*;

import com.db4o.bench.logging.*;
import com.db4o.io.IoAdapter;


public class LogReplayer {
	
	private String _logFilePath;
	private IoAdapter _io;
	private Set _commands;
	
	public LogReplayer(String logFilePath, IoAdapter io, Set commands) {
		_logFilePath = logFilePath;
		_io = io;
		_commands = commands;
	}
	
	public LogReplayer(String logFilePath, IoAdapter io) {
		this(logFilePath, io, LogConstants.allEntries());
	}
	
	public void setLog(String logFilePath) {
		_logFilePath = logFilePath;
	}
	
	public void replayLog() throws IOException {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(_logFilePath));
			String line = null;
			while ( (line = reader.readLine()) != null ) {
				replayLine(line);
			}
			reader.close();
		} finally {
			_io.close();
		}
	}

	private void replayLine(String line) {
		if ( line.startsWith(LogConstants.WRITE_ENTRY) ) {
			if (_commands.contains(LogConstants.WRITE_ENTRY)) {
				replayWrite(line);
			}
			return;
		}
		if ( line.startsWith(LogConstants.READ_ENTRY) ) {
			if (_commands.contains(LogConstants.READ_ENTRY)) {
				replayRead(line);
			}
			return;
		}
		if ( line.startsWith(LogConstants.SYNC_ENTRY) ) {
			if (_commands.contains(LogConstants.SYNC_ENTRY)) {
				replaySync();
			}
			return;
		}
		if ( line.startsWith(LogConstants.SEEK_ENTRY) ) {
			if (_commands.contains(LogConstants.SEEK_ENTRY)) {
				replaySeek(line);
			}
			return;
		}
		throw new IllegalArgumentException("Unknown command in log: " + line);
	}

	private void replaySync() {
		_io.sync();
	}
	
	private void replayRead(String line) {
		byte[] buffer = prepareBuffer(LogConstants.READ_ENTRY, line);
		_io.read(buffer, buffer.length);
	}

	private void replayWrite(String line) {
		byte[] buffer = prepareBuffer(LogConstants.WRITE_ENTRY, line);		
		_io.write(buffer);
	}
	
	private void replaySeek(String line) {
		long pos = parameter(LogConstants.SEEK_ENTRY.length(), line);
		_io.seek(pos);
	}

	private byte[] prepareBuffer(String command, String line) {
		int length = (int) parameter(command.length(),  line);
		return new byte[length];
	}

	private long parameter(int start, String line) {
		return Long.parseLong(line.substring(start));
	}
}
