/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import java.io.*;

public class EventConfiguration {
	
	public final String databaseName;
	
	public final String logFileName;
	
	public final int daemonPort;
	
	public final int clientPort;
	
	public EventConfiguration(String databaseName, String logFileName, int daemonPort, int clientPort) {
		this.databaseName = databaseName;
		this.logFileName = logFileName;
		this.daemonPort = daemonPort;
		this.clientPort = clientPort;
	}

	public void writeConfigFile(File file) throws IOException {
		FileWriter fileWriter = new FileWriter(file);
		PrintWriter writer = new PrintWriter(fileWriter);
		writer.println("ChannelServicePort " + daemonPort);
		writer.println("Log " + logFileName);
		writer.println("LogLevel 2 ");
		
		System.err.println("Channel engine is only configured for windows here.");
		
		writer.println("<EngineLibs>");
		writer.println("vedse.dll");
		writer.println("</EngineLibs>");
		

		
		writer.flush();
		writer.close();
	}

}
