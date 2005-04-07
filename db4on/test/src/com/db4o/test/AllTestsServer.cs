/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test {

    /// <summary>
    /// This class will start a dedicated server for AllTests to run tests
    /// on different machines.
    /// </summary>
    ///
   public class AllTestsServer : AllTests, Runnable {
      
      public AllTestsServer() : base() {
      }
      
      public static void Main(String[] args) {
         new AllTestsServer().run();
      }
      
      public override void run() {
         Db4o.configure().messageLevel(-1);
         logConfiguration();
         Console.WriteLine("Waiting for tests to be run from different machine.");
         Console.WriteLine("\n\nThe server will need to be closed with CTRL + C.\n\n");
         Test.delete();
         configure();
         Test.runServer = true;
         Test.clientServer = true;
         Test.open();
      }
   }
}
