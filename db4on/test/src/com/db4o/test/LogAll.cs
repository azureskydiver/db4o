/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.tools;
namespace com.db4o.test {

   public class LogAll {
      
      public LogAll() : base() {
      }
      
      public static void Main(String[] args) {
         run(args[0]);
      }
      
      public static void run(String fileName) {
         Console.WriteLine("/** Logging database file: \'" + fileName + "\' **/");
         ObjectContainer con1 = Db4o.openFile(fileName);
         ObjectSet set1 = con1.get(null);
         while (set1.hasNext()) {
            Logger.log(con1, set1.next());
         }
         con1.close();
      }
   }
}