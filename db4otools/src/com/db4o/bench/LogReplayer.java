/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench;

import java.io.*;

import com.db4o.io.IoAdapter;


public class LogReplayer {
	
/**
 * TODO: These constants depend on LoggingIoAdapter.
 * Possible to factor them out? Or make public in LIA?
 */	
	private static final String	WRITE = 		"WRITE";
	private static final String READ  = 		"READ";
	private static final String SYNC  = 		"SYNC";
	private static final char 	SEPARATOR =		',';
	private static final int	WRITE_INDEX =	6;
	private static final int	READ_INDEX = 	5;

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
		if ( line.startsWith(WRITE) ) {
			replayWrite(line);
		}
		else if ( line.startsWith(READ) ) {
			replayRead(line);
		}
		else if ( line.startsWith(SYNC) ) {
			replaySync();
		}
		else {
			// TODO: unknown command. how to react??
		}
		
	}

	private void replaySync() {
		_io.sync();
	}

	
/**
 * TODO: Code of replayRead and replayWrite very similar.
 * Possible / sensible to factor out common things?
 */	
	
	private void replayRead(String line) {
		// format of line: "READ startPos,length"
		// 5 = READ_INDEX  ------^       ^-- separator 
		
		int separator = separatorIndexForLine(line);
		long pos = posForLine(READ_INDEX, separator, line);
		_io.seek(pos);
		
		int length = lengthForLine(separator, line);
		byte[] buffer = new byte[length];
		_io.read(buffer, length);
	}

	private void replayWrite(String line) {
		// format of line: "WRITE startPos,length"
		// 6 = WRITE_INDEX  ------^       ^-- separator
		
		int separator = separatorIndexForLine(line);
		long pos = posForLine(WRITE_INDEX, separator, line);
		_io.seek(pos);
		
		int length = lengthForLine(separator, line);
		// TODO: use actual byte sequence from log?
		byte[] buffer = new byte[length];
		_io.write(buffer);
	}
	
	private int separatorIndexForLine(String line) {
		return line.indexOf(SEPARATOR);
	}
	
	private int lengthForLine(int separator, String line) {
		return Integer.parseInt(line.substring(separator+1));
	}

	private long posForLine(int start, int separator, String line) {
		return Long.parseLong(line.substring(start, separator));
	}
}
