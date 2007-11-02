/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench;

import java.io.*;

import com.db4o.io.IoAdapter;


public class LogReplayer {
	

	private String _logFilePath;
	private IoAdapter _io;
	
	public LogReplayer(String logFilePath, IoAdapter io) {
		_logFilePath = logFilePath;
		_io = io;
	}
	
	public void setLog(String logFilePath) {
		_logFilePath = logFilePath;
	}
	
	public void replayLog() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(_logFilePath));
			String line = null;
			while ( (line = reader.readLine()) != null ) {
				replayLine(line);
			}
		} catch (FileNotFoundException fne) {
			// TODO Auto-generated catch block
			fne.printStackTrace();
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		}
	}

	private void replayLine(String line) {
		if ( line.startsWith(LoggingIoAdapter.WRITE_ENTRY) ) {
			replayWrite(line);
		}
		else if ( line.startsWith(LoggingIoAdapter.READ_ENTRY) ) {
			replayRead(line);
		}
		else if ( line.startsWith(LoggingIoAdapter.SYNC_ENTRY) ) {
			replaySync();
		}
		else {
			// TODO: unknown command. how to react??
		}
		
	}

	private void replaySync() {
		_io.sync();
	}

	
	private void replayRead(String line) {
		byte[] buffer = prepareCommand(LoggingIoAdapter.READ_ENTRY, line);
		_io.read(buffer, buffer.length);
	}

	private void replayWrite(String line) {
		byte[] buffer = prepareCommand(LoggingIoAdapter.WRITE_ENTRY, line);		
		_io.write(buffer);
	}
	
	private byte[] prepareCommand(String command, String line) {
		int separatorIndex = separatorIndexForLine(line);
		long pos = posForLine(command.length(), separatorIndex, line);
		_io.seek(pos);
		
		int length = lengthForLine(separatorIndex, line);
		return new byte[length];
	}
	
	private int separatorIndexForLine(String line) {
		return line.indexOf(LoggingIoAdapter.SEPARATOR);
	}
	
	private int lengthForLine(int separator, String line) {
		return Integer.parseInt(line.substring(separator+1));
	}

	private long posForLine(int start, int separator, String line) {
		return Long.parseLong(line.substring(start, separator));
	}
}
