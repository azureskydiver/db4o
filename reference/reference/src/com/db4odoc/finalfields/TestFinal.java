/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.finalfields;
import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

public class TestFinal
{
	private static final String DB4O_FILE_NAME = "reference.db4o";
	// non-final fields
	public int    _i;
	public String _s;
   // final fields storing the same values as above
	public final  int    _final_i;
	public final String _final_s;
	
   public static void main(String[] args)
   {
      new File(DB4O_FILE_NAME).delete();
      ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
      try {
    	  TestFinal test = new TestFinal(1,"test");
    	  container.store(test);
    	  System.out.println("Added: " + test);
      } finally {
    	  // Close does implicit commit and refreshes the reference cache
    	  container.close();
      }
      container = Db4o.openFile(DB4O_FILE_NAME);
      try {
    	  ObjectSet result = container.queryByExample(null);
    	  listResult(result);
      } finally { 
    	  container.close();
      }
   }
   // end main
   
   public TestFinal(int i, String s)
   {
	   // initialize final and non-final fields with the same values
      _i       = i;
      _s       = s;
      _final_i = i;
      _final_s = s;
   }
   // end TestFinal

   public String toString()
   {
      return "Int - " + _i + "; String - " + _s + "; FINAL Int - " + _final_i + "; FINAL String - " + _final_s;
   }
   // end toString
   
   private static void listResult(ObjectSet result)
   {
      while(result.hasNext()) {
         System.out.println(result.next());
     }
   }
   // end listResult
}