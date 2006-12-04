/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.selpersist;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;


public class MarkTransientExample {
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
			Test test = new Test("Transient string","Persistent string");
			oc.set(test);
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
			ObjectSet result = oc.query(Test.class);
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
