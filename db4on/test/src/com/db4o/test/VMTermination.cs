/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test {

   public class VMTermination {
      private String str;
      
      public VMTermination() : base() {
      }
      
      public VMTermination(String str) : base() {
         this.str = str;
      }
      
      public static void Main(String[] args) {
         int step1 = 0;
         switch (step1) {
         case 0:
            Tester.PrintStatistics();
            break;
         
         case 1:
            KillSingleUser();
            break;
         
         case 2:
            TestSingleUser();
            break;
         
         case 3:
            KillServer1();
            break;
         
         case 4:
            TestServer1();
            break;
         
         case 5:
            KillServer2();
            break;
         
         case 6:
            TestServer2();
            break;
         
         }
      }
      
      public static void KillSingleUser() {
         Tester.runServer = false;
         Tester.clientServer = false;
         Tester.Delete();
         ObjectContainer con1 = Tester.Open();
         con1.Set(new VMTermination("willbethere"));
         con1.Commit();
         // Environment.Exit(0);
      }
      
      public static void TestSingleUser() {
         Tester.runServer = false;
         Tester.clientServer = false;
         ObjectContainer con1 = Tester.Open();
         Tester.EnsureOccurrences(new VMTermination(), 1);
         Tester.LogAll();
         Tester.End();
      }
      
      public static void KillServer1() {
         Tester.runServer = true;
         Tester.clientServer = true;
         Tester.Delete();
         ObjectContainer con1 = Tester.Open();
         con1.Set(new VMTermination("willbethere"));
         con1.Commit();
         // Environment.Exit(0);
      }
      
      public static void TestServer1() {
         Tester.runServer = true;
         Tester.clientServer = true;
         ObjectContainer con1 = Tester.Open();
         Tester.EnsureOccurrences(new VMTermination(), 1);
         Tester.LogAll();
         Tester.End();
      }
      
      public static void KillServer2() {
         Tester.runServer = true;
         Tester.clientServer = true;
         ObjectContainer con1 = Tester.Open();
         con1.Set(new VMTermination("willbethere"));
         con1.Commit();
      }
      
      public static void TestServer2() {
         Tester.runServer = true;
         Tester.clientServer = true;
         ObjectContainer con1 = Tester.Open();
         Tester.EnsureOccurrences(new VMTermination(), 2);
         Tester.LogAll();
         Tester.End();
      }
   }
}
