/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.selectivepersistence;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;


public class MarkTransientExample {
	private final static String DB4O_FILE_NAME = "reference.db4o";

	public static void main(String[] args)
	{
		saveObjects(configureSaveTransient());
		retrieveObjects();
	}
	// end main

	private static Configuration configureSaveTransient(){
		Configuration configuration = Db4o.newConfiguration();
		configuration.objectClass(Test.class).storeTransientFields(true);
		return configuration;
	}
	// end configureSaveTransient
	
	private static void saveObjects(Configuration configuration){
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(configuration, DB4O_FILE_NAME);
		try 
		{
			Test test = new Test("Transient string","Persistent string");
			container.store(test);
		} 
		finally 
		{
			container.close();
		}
	}
	// end saveObjects

	private static void retrieveObjects()
	{
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try 
		{
			ObjectSet result = container.query(Test.class);
			listResult(result);
		} 
		finally 
		{
			container.close();
		}
	}
	// end retrieveObjects

	private static void listResult(ObjectSet result)
	{
		System.out.println(result.size());
		for(int x = 0; x < result.size(); x++)
			System.out.println(result.get(x));
	}
	// end listResult
}
