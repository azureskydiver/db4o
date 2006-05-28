/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

using Db4oTools;

using j4o.lang;

using com.db4o;

namespace com.db4o.test {

   public class LogAll {
      
      public LogAll() : base() {
      }
      
      public static void Main(String[] args) {
         Run(args[0]);
      }
      
      public static void Run(String fileName) {
         Console.WriteLine("/** Logging database file: \'" + fileName + "\' **/");
         ObjectContainer con1 = Db4o.OpenFile(fileName);
         ObjectSet set1 = con1.Get(null);
         while (set1.HasNext()) {
            Logger.Log(con1, set1.Next());
         }
         con1.Close();
      }
   }
}