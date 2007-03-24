/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.selpersist;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;


public class TransientClassExample {

	public final static String YAPFILENAME = "formula1.yap";
	
	public static void main(String[] args)
	{
		saveObjects();
		retrieveObjects();
	}
	// end main

	public static void saveObjects(){
		new File(YAPFILENAME).delete();
		ObjectContainer oc = Db4o.openFile(YAPFILENAME);
		try 
		{
			// Save Test1 object with a NotStorable class field
			Test1 test1 = new Test1("Test1", new NotStorable());
			oc.set(test1);
			// Save Test2 object with a NotStorable class field
			Test2 test2 = new Test2("Test2", new NotStorable(), test1);
			oc.set(test2);
		} 
		finally 
		{
			oc.close();
		}
	}
	// end saveObjects

	public static void retrieveObjects()
	{
		ObjectContainer oc = Db4o.openFile(YAPFILENAME);
		try 
		{
			// retrieve the results and check if the NotStorable instances were saved
			ObjectSet result = oc.get(null);
			listResult(result);
		} 
		finally 
		{
			oc.close();
		}
	}
	// end retrieveObjects

	public static void listResult(ObjectSet result)
	{
		System.out.println(result.size());
		for(int x = 0; x < result.size(); x++)
			System.out.println(result.get(x));
	}
	// end listResult


}
