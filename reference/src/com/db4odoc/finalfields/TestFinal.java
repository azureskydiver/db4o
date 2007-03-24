package com.db4odoc.finalfields;
import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

public class TestFinal
{
	// non-final fields
	public int    _i;
	public String _s;
   // final fields storing the same values as above
	public final  int    _final_i;
	public final String _final_s;
	
   public static void main(String[] args)
   {
      new File("test.yap").delete();
      ObjectContainer db = Db4o.openFile("test.yap");
      try {
    	  TestFinal test = new TestFinal(1,"test");
    	  db.set(test);
    	  System.out.println("Added: " + test);
      } finally {
    	  // Close does implicit commit and refreshes the reference cache
    	  db.close();
      }
      db = Db4o.openFile("test.yap");
      try {
    	  ObjectSet result = db.get(null);
    	  listResult(result);
      } finally { 
    	  db.close();
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
   
   public static void listResult(ObjectSet result)
   {
      while(result.hasNext()) {
         System.err.println(result.next());
     }
   }
   // end listResult
}