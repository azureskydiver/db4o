/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.jdo.*;

import com.db4o.drs.inside.*;
import com.db4o.util.*;
import com.versant.odbms.*;
import com.versant.util.*;

public class VodDatabase {
	
	private static final String CONNECTION_URL_KEY = "javax.jdo.option.ConnectionURL";

	private static final int DEFAULT_PORT = 5019;

	private final String _name;
	
	private final Properties _properties;
	
	private PersistenceManagerFactory _persistenceManagerFactory;
	
	private DatastoreManagerFactory _datastoreManagerFactory;

	public VodDatabase(String name, Properties properties){
		_name = name;
		_properties = properties;
		amendDefaultProperties();
	}
	
	public VodDatabase(String name){
		this(name, new Properties());
	}
	
	private void amendDefaultProperties(){
		amendPropertyIfNotExists(CONNECTION_URL_KEY, "versant:" + _name + "@localhost");
		amendPropertyIfNotExists("javax.jdo.PersistenceManagerFactoryClass","com.versant.core.jdo.BootstrapPMF");
	}
	
	public void amendPropertyIfNotExists(String key, String value) {
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
	
	public void createDb(){
		if(dbExists()){
			return;
		}
		Properties p=new Properties();
		DBUtility.makeDB(_name,p);
		DBUtility.createDB(_name);
	}

	public PersistenceManager createPersistenceManager() {
		return persistenceManagerFactory().getPersistenceManager();
	}
	
	private PersistenceManagerFactory persistenceManagerFactory(){
		if(_persistenceManagerFactory == null){
			_persistenceManagerFactory = JDOHelper.getPersistenceManagerFactory(_properties);
		}
		return _persistenceManagerFactory;
	}
	
	public void enhance(String outDir) throws IOException, InterruptedException{
		
		File outDirFile = new File(outDir);
		
		String tempFileName = Path4.getTempFileName();
		File tempFile = new File(tempFileName);
		try{
			FileWriter writer = new FileWriter(tempFile);
			_properties.store(writer, null);
			writer.close();
			
			System.out.println(outDirFile.getAbsolutePath());

			
			String[] args = new String[]{tempFile.getAbsolutePath(), outDirFile.getAbsolutePath()};
			
			String output = JavaServices.java("com.db4o.drs.versant.VodEnhancer", args);
			if(DrsDebug.verbose){
				System.out.println(output);
			}
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
			amendDefaultProperties();
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

}
