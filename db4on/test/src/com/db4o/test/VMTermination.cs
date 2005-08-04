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
            Tester.printStatistics();
            break;
         
         case 1:
            killSingleUser();
            break;
         
         case 2:
            testSingleUser();
            break;
         
         case 3:
            killServer1();
            break;
         
         case 4:
            testServer1();
            break;
         
         case 5:
            killServer2();
            break;
         
         case 6:
            testServer2();
            break;
         
         }
      }
      
      public static void killSingleUser() {
         Tester.runServer = false;
         Tester.clientServer = false;
         Tester.delete();
         ObjectContainer con1 = Tester.open();
         con1.set(new VMTermination("willbethere"));
         con1.commit();
         // Environment.Exit(0);
      }
      
      public static void testSingleUser() {
         Tester.runServer = false;
         Tester.clientServer = false;
         ObjectContainer con1 = Tester.open();
         Tester.ensureOccurrences(new VMTermination(), 1);
         Tester.logAll();
         Tester.end();
      }
      
      public static void killServer1() {
         Tester.runServer = true;
         Tester.clientServer = true;
         Tester.delete();
         ObjectContainer con1 = Tester.open();
         con1.set(new VMTermination("willbethere"));
         con1.commit();
         // Environment.Exit(0);
      }
      
      public static void testServer1() {
         Tester.runServer = true;
         Tester.clientServer = true;
         ObjectContainer con1 = Tester.open();
         Tester.ensureOccurrences(new VMTermination(), 1);
         Tester.logAll();
         Tester.end();
      }
      
      public static void killServer2() {
         Tester.runServer = true;
         Tester.clientServer = true;
         ObjectContainer con1 = Tester.open();
         con1.set(new VMTermination("willbethere"));
         con1.commit();
      }
      
      public static void testServer2() {
         Tester.runServer = true;
         Tester.clientServer = true;
         ObjectContainer con1 = Tester.open();
         Tester.ensureOccurrences(new VMTermination(), 2);
         Tester.logAll();
         Tester.end();
      }
   }
}
