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
            Test.statistics();
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
         Test.runServer = false;
         Test.clientServer = false;
         Test.delete();
         ObjectContainer con1 = Test.open();
         con1.set(new VMTermination("willbethere"));
         con1.commit();
         // Environment.Exit(0);
      }
      
      public static void testSingleUser() {
         Test.runServer = false;
         Test.clientServer = false;
         ObjectContainer con1 = Test.open();
         Test.ensureOccurrences(new VMTermination(), 1);
         Test.logAll();
         Test.end();
      }
      
      public static void killServer1() {
         Test.runServer = true;
         Test.clientServer = true;
         Test.delete();
         ObjectContainer con1 = Test.open();
         con1.set(new VMTermination("willbethere"));
         con1.commit();
         // Environment.Exit(0);
      }
      
      public static void testServer1() {
         Test.runServer = true;
         Test.clientServer = true;
         ObjectContainer con1 = Test.open();
         Test.ensureOccurrences(new VMTermination(), 1);
         Test.logAll();
         Test.end();
      }
      
      public static void killServer2() {
         Test.runServer = true;
         Test.clientServer = true;
         ObjectContainer con1 = Test.open();
         con1.set(new VMTermination("willbethere"));
         con1.commit();
      }
      
      public static void testServer2() {
         Test.runServer = true;
         Test.clientServer = true;
         ObjectContainer con1 = Test.open();
         Test.ensureOccurrences(new VMTermination(), 2);
         Test.logAll();
         Test.end();
      }
   }
}