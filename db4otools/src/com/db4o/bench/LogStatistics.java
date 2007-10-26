/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench;

import java.io.*;
import java.text.*;
import java.util.*;


public class LogStatistics {

	private String _logFilePath;
	private String _statisticsFilePath;
	private PrintStream _out;
	private BufferedReader _in;
	
	private long _readCount = 0;
	private long _readBytes = 0;
	private long _writeCount = 0;
	private long _writeBytes = 0;
	private long _syncCount = 0;
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if ( args.length < 1 ) {
			System.out.println("Usage: LogStatistics <log file path> [<statistics file path>]");
		}
		else {
			String statisticsFilePath = args[0] + "-stat.htm";
			if ( args.length > 1 ) {
				statisticsFilePath = args[1];
			}
			new LogStatistics(args[0], statisticsFilePath);
		}

	}
	
	
	public LogStatistics(String logFilePath, String statisticsFilePath) {
		_logFilePath = logFilePath;
		_statisticsFilePath = statisticsFilePath;
		
		try {
			openStatisticsFile();
			openLogFile();
			
			System.out.println("Creating statistics for " + _logFilePath);
			
			long start = System.currentTimeMillis();
			createStatistics();
			long end = System.currentTimeMillis();
			
			Date date = new Date(end-start);
			SimpleDateFormat sdf = new SimpleDateFormat("mm:ss.S");
			String elapsed = sdf.format(date);
			
			System.out.println("Time taken: " + elapsed);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		closeStatisticsFile();
		closeLogFile();
	}

	private void closeLogFile() {
		try {
			_in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void closeStatisticsFile() {
		_out.flush();
		_out.close();
	}


	private void openLogFile() throws FileNotFoundException {
		_in = new BufferedReader(new FileReader(_logFilePath));
	}


	private void openStatisticsFile() throws FileNotFoundException {
		_out = new PrintStream(new FileOutputStream(_statisticsFilePath));
	}


	private void createStatistics() {
		String line;
		try {
			while ( (line = _in.readLine()) != null ) {
				handleLine(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		outputStatistics();
	}


	private void outputStatistics() {
		DecimalFormat formatPercentage = new DecimalFormat("##.##");
		DecimalFormat formatCount = new DecimalFormat("###,###");
		
		long totalCount = _readCount + _writeCount + _syncCount;
		String totalCountString = formatCount.format(totalCount);
		
		double readCountPercentage = 100 * (double)_readCount / (double)totalCount;
		String readCountPercentageString = formatPercentage.format(readCountPercentage);
		
		double writeCountPercentage = 100 *  (double)_writeCount / (double)totalCount;
		String writeCountPercentageString = formatPercentage.format(writeCountPercentage);
		
		double syncCountPercentage = 100 * (double)_syncCount / (double)totalCount;
		String syncCountPercentageString = formatPercentage.format(syncCountPercentage);
		
		long totalBytes = _readBytes + _writeBytes;
		String totalBytesString = formatCount.format(totalBytes);
		
		double readBytesPercentage = 100 * (double)_readBytes / (double)totalBytes;
		String readBytesPercentageString = formatPercentage.format(readBytesPercentage);
		
		double writeBytesPercentage = 100 * (double)_writeBytes / (double)totalBytes;
		String writeBytesPercentageString = formatPercentage.format(writeBytesPercentage);
		
		String readCountString = formatCount.format(_readCount);
		String writeCountString = formatCount.format(_writeCount);
		String syncCountString = formatCount.format(_syncCount);
		
		String readBytesString = formatCount.format(_readBytes);
		String writeBytesString = formatCount.format(_writeBytes);
		
		_out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		_out.println("<html>");
		_out.println("<head>");
		_out.println("<title>Log Statistics</title><meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">");
		_out.println("</head>");
		_out.println("<body>");
		_out.println("<p>Statistics for logfile '" + _logFilePath + "'</p>");
		_out.println("<table border=\"0\" cellpadding=\"4\">");
		_out.println("<tr><th></th><th></th><th>Count</th><th></th><th>%</th><th></th><th>Bytes</th><th></th><th>%</th></tr>");
		_out.println("<tr><td>Reads</td><td></td><td align=\"right\">" + readCountString + "</td><td></td><td align=\"right\">" + readCountPercentageString + "</td><td></td><td align=\"right\">" + readBytesString + "</td><td></td><td align=\"right\">" + readBytesPercentageString + "</td></tr>");
		_out.println("<tr><td>Writes</td><td></td><td align=\"right\">" + writeCountString + "</td><td></td><td align=\"right\">" + writeCountPercentageString + "</td><td></td><td align=\"right\">" + writeBytesString + "</td><td></td><td align=\"right\">" + writeBytesPercentageString + "</td></tr>");
		_out.println("<tr><td>Syncs</td><td></td><td align=\"right\">" + syncCountString + "</td><td></td><td align=\"right\">" + syncCountPercentageString + "</td><td></td><td></td></tr>");
		_out.println("<tr><td colspan=\"9\"></td></tr>");
		_out.println("<tr><td>Total</td><td></td><td align=\"right\">" + totalCountString + "</td><td></td><td></td><td></td><td>" + totalBytesString + "</td><td></td></tr>");
		_out.println("</table>");
		
		double avgBytesPerRead = _readBytes / _readCount;
		String avgBytesPerReadString = formatCount.format(avgBytesPerRead);
		double avgBytesPerWrite = _writeBytes / _writeCount;
		String avgBytesPerWriteString = formatCount.format(avgBytesPerWrite);
		
		_out.println("<p>");
		_out.println("Average byte count per read: " + avgBytesPerReadString);
		_out.println("<br>");
		_out.println("Average byte count per write: " + avgBytesPerWriteString);
		_out.println("</p>");
		
		_out.println("</body>");
		_out.println("</html>");
	}


	private void handleLine(String line) {
		if ( line.startsWith(LoggingIoAdapter.WRITE_ENTRY) ) {
			handleWrite(line);
		}
		else if ( line.startsWith(LoggingIoAdapter.READ_ENTRY) ) {
			handleRead(line);
		}
		else if ( line.startsWith(LoggingIoAdapter.SYNC_ENTRY) ) {
			handleSync();
		}
		else {
			// TODO: unknown command. how to react??
		}
	}


	private void handleSync() {
		_syncCount++;
	}


	private void handleRead(String line) {
		_readCount++;
		_readBytes += bytesForLine(line);
	}


	private void handleWrite(String line) {
		_writeCount++;
		_writeBytes += bytesForLine(line);
	}


	private long bytesForLine(String line) {
		int separatorIndex = line.indexOf(LoggingIoAdapter.SEPARATOR);
		long length = Long.parseLong(line.substring(separatorIndex+1));
		return length;
	}

	

}
