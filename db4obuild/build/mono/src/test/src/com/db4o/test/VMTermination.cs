/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

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