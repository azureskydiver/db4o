/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import java.io.*;
import java.util.*;

import javax.jdo.*;

import com.db4o.drs.inside.*;
import com.db4o.util.*;
import com.versant.util.*;

public class VodDatabase {
	
	private final String _name;
	
	private final Properties _properties;
	
	private PersistenceManagerFactory _persistenceManagerFactory; 

	public VodDatabase(String name, Properties properties){
		_name = name;
		_properties = properties;
		amendDefaultProperties();
	}
	
	public VodDatabase(String name){
		this(name, new Properties());
	}
	
	private void amendDefaultProperties(){
		amendPropertyIfNotExists("javax.jdo.option.ConnectionURL", "versant:" + _name + "@localhost");
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
	

}
