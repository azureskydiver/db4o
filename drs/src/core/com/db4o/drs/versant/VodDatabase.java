/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.jdo.*;

import com.db4o.drs.inside.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.eventlistener.Program.*;
import com.db4o.util.*;
import com.db4o.util.IOServices.*;
import com.versant.odbms.*;
import com.versant.util.*;

public class VodDatabase {
	
	
	private static final String CONNECTION_URL_KEY = "javax.jdo.option.ConnectionURL";
	
	private static final String JDO_METADATA_KEY = "versant.metadata."; 

	public static final String VED_DRIVER = "veddriver";
	

	private static final int DEFAULT_PORT = 5019;

	private final String _name;
	
	private final Properties _properties;
	
	private PersistenceManagerFactory _persistenceManagerFactory;
	
	private DatastoreManagerFactory _datastoreManagerFactory;
	
	private static int nextPort = 4100;
	
	private VodEventDriver _eventDriver;
	
	private EventConfiguration _eventConfiguration;

	private EventProcessorSupport _eventProcessorSupport;
	

	public VodDatabase(String name, Properties properties){
		_name = name;
		_properties = properties;
		addDefaultProperties();
	}
	
	public VodDatabase(String name){
		this(name, new Properties());
	}
	
	private void addDefaultProperties(){
		
		addPropertyIfNotExists("versant.l2CacheEnabled", "false");
		
		/**
		 * Setting the following property produces fully qualified classnames.
		 * VOD also uses fully qualified classnames, if they are fully qualified
		 * in the .jdo file.
		 */
		// addPropertyIfNotExists("versant.vdsNamingPolicy", "none");
		
		addPropertyIfNotExists(CONNECTION_URL_KEY, "versant:" + _name + "@localhost");
		addPropertyIfNotExists("javax.jdo.PersistenceManagerFactoryClass","com.versant.core.jdo.BootstrapPMF");
		addJdoMetaDataFiles();
	}
	
	public void addPropertyIfNotExists(String key, String value) {
		if(_properties.containsKey(key)){
			return;
		}
		_properties.setProperty(key, value);
	}

	public boolean dbExists(){
		DBListInfo[] dbList = DBUtility.dbList();
		for (DBListInfo dbListInfo : dbList) {
			String name = dbListInfo.getDBName();
			
			int indexOfHostName = name.indexOf("@");
			if(indexOfHostName >= 0){
				name = name.substring(0, indexOfHostName);
			}
			
			if(_name.equals(name)){
				return true;
			}
		}
		return false;
	}
	
	public void removeDb() {
		if(! dbExists()){
			return;
		}
		Properties props = new Properties();
		props.put ("-f","");
		try{
			DBUtility.stopDB(_name,props);
		} catch(Exception ex){
			ex.printStackTrace();
		}
		props.put("-rmdir","");
		try{
			DBUtility.removeDB(_name, props);
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void produceDb(){
		if(dbExists()){
			return;
		}
		Properties p=new Properties();
		DBUtility.makeDB(_name,p);
		DBUtility.createDB(_name);
	}

	public PersistenceManagerFactory persistenceManagerFactory(){
		if(_persistenceManagerFactory == null){
			_persistenceManagerFactory = JDOHelper.getPersistenceManagerFactory(_properties);
		}
		return _persistenceManagerFactory;
	}
	
	public void enhance() {
		String tempFileName = Path4.getTempFileName();
		File tempFile = new File(tempFileName);
		try{
			FileWriter writer = new FileWriter(tempFile);
			if(DrsDebug.verbose){
				_properties.store(System.err, null);
			}
			_properties.store(writer, null);
			writer.close();
			
			String[] args = new String[]{tempFile.getAbsolutePath()};
			
			ProcessResult processResult = JavaServices.java("com.db4o.drs.versant.VodEnhancer", args);
			if(DrsDebug.verbose){
				System.out.println(processResult);
			}
		} catch(IOException ioe){
			throw new RuntimeException(ioe);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			tempFile.delete();
		}
	}

	public DatastoreManager createDatastoreManager() {
		return datastoreManagerFactory().getDatastoreManager();
	}
	
	private DatastoreManagerFactory datastoreManagerFactory(){
		if(_datastoreManagerFactory == null){
			ConnectionInfo con = new ConnectionInfo(_name, host(), port(), userName(), passWord());
			_datastoreManagerFactory = new DatastoreManagerFactory(con, new ConnectionProperties());
		}
		return _datastoreManagerFactory;
	}
	
	private String userName(){
        return _properties.getProperty("javax.jdo.option.ConnectionUserName");
	}
	
	private String passWord(){
		return _properties.getProperty("javax.jdo.option.ConnectionPassword");
	}
	
	private int port(){
		int port = connectionUrl().getPort();
		if(port != -1){
			return port;
		}
		return DEFAULT_PORT;
	}
	
	private String host(){
		return connectionUrl().getHost();
	}

	private String connectionUrlAsString() {
		String connectionURL = _properties.getProperty(CONNECTION_URL_KEY);
		if(connectionURL == null){
			addDefaultProperties();
		}
		connectionURL = _properties.getProperty(CONNECTION_URL_KEY);
		return connectionURL.replaceAll("versant:", "http:");
	}
	
	private URL connectionUrl(){
		try {
			return new URL(connectionUrlAsString());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void addJdoMetaDataFiles() {
		for (File file : new File("bin").listFiles()) {
			if(! file.isDirectory()  && file.getName().endsWith(".jdo")){
				addJdoMetaDataFile(file.getName());
			}
		}
	}

	public void addJdoMetaDataFile(String fileName) {
		
		final int maxMetadataEntries = 1000;
		final int quitSearchingAfterGap = 5;
		final int notSet = -1;
		
		int freeEntry = notSet;
		int lastOccupied = notSet;
		
		for (int i = 0; i < maxMetadataEntries; i++) {
			String property = _properties.getProperty(JDO_METADATA_KEY + i);
			if(property == null){
				if(freeEntry == notSet){
					freeEntry = i;
				}
				if(i - lastOccupied > quitSearchingAfterGap){
					break;
				}
			} else {
				lastOccupied = i;
				if(fileName.equals(property)){
					return;
				}
			}
		}
		_properties.setProperty(JDO_METADATA_KEY + freeEntry, fileName);
	}
	
	public void startEventDriver() {
		if(_eventDriver != null){
			throw new IllegalStateException("Event driver can only be started once.");
		}
		int serverPort = nextPort();
		EventClientPortSelectionStrategy clientPortStrategy = new IncrementingEventClientPortSelectionStrategy(nextPort());
		_eventConfiguration = new EventConfiguration(_name, _name + "event.log",  "localhost", serverPort, "localhost", clientPortStrategy, true);
		_eventDriver = new VodEventDriver(_eventConfiguration);
		boolean started = _eventDriver.start();
		if(! started ){
			_eventDriver.printStartupFailure();
			_eventDriver = null;
			throw new IllegalStateException();
		}
	}

	private static int nextPort() {
		return nextPort++;
	}
	
	public void createEventSchema() {
		VodJvi jvi = new VodJvi(this);
		jvi.createEventSchema();
		jvi.close();
	}
	
	public String databaseName(){
		return _name;
	}
	
	@Override
	public String toString() {
		return "VOD " + _name;
	}

	public void stopEventDriver() {
		_eventDriver.stop();
		_eventDriver = null;
	}
	
	public EventProcessorSupport startEventProcessor(){
		if(_eventProcessorSupport != null){
			throw new IllegalStateException();
		}
		_eventProcessorSupport = new EventProcessorSupport(_eventConfiguration);
		return _eventProcessorSupport;
	}
	
	public void stopEventProcessor(){
		_eventProcessorSupport.stop();
		_eventProcessorSupport = null;
	}
	
	public ProcessRunner startEventProcessorInSeparateProcess() throws IOException {
		
		List<String> arguments = new ArrayList<String>();
		
		addArgument(arguments, Arguments.DATABASE, _name);
		addArgument(arguments, Arguments.LOGFILE, _eventConfiguration.logFileName);
		addArgument(arguments, Arguments.SERVER_PORT, _eventConfiguration.serverPort);
		addArgument(arguments, Arguments.CLIENT_PORT, _eventConfiguration.clientPort());
		
		String[] argumentsAsString = new String[arguments.size()];
		argumentsAsString = arguments.toArray(argumentsAsString);
		
		ProcessRunner eventListenerProcess = JavaServices.startJava(Program.class.getName(), argumentsAsString);
		eventListenerProcess.waitFor(_name, 10000);
		return eventListenerProcess;
	}
	
	private static void addArgument(List<String> arguments, String argumentName, int argumentValue) {
		addArgument(arguments, argumentName, String.valueOf(argumentValue));
	}

	private static void addArgument(List<String> arguments, String argumentName, String argumentValue) {
		addArgument(arguments, argumentName);
		arguments.add(argumentValue);
	}
	
	private static void addArgument(List<String> arguments, String argumentName) {
		arguments.add("-" + argumentName);
	}
	
	public EventConfiguration eventConfiguration(){
		return _eventConfiguration;
	}
	
}
