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
using j4o.io;
using com.db4o;
namespace com.db4o.test {

   public class ReadOnly : Runnable {
      
      public ReadOnly() : base() {
      }
      private static String FILE = "readonly.yap";
      private static int COUNT = 100;
      private static String MY_STRING = "ReadOnly test instance ";
      public String myString;
      
      public static void Main(String[] args) {
         Db4o.configure().readOnly(true);
         new ReadOnly().spendSomeTime();
         Db4o.configure().readOnly(false);
      }
      
      public void run() {
         setUp();
         test();
         Db4o.configure().readOnly(false);
      }
      
      private void setUp() {
         new File(FILE).delete();
         ObjectContainer con1 = Db4o.openFile(FILE);
         for (int i1 = 0; i1 < COUNT; i1++) {
            ReadOnly ro1 = new ReadOnly();
            ro1.myString = MY_STRING + i1;
            con1.set(ro1);
         }
         con1.close();
      }
      
      private void test() {
         Db4o.configure().readOnly(true);
         checkCount();
         ObjectContainer con1 = Db4o.openFile(FILE);
         con1.set(new ReadOnly());
         con1.close();
         checkCount();
         try {
            {
            }
         }  catch (Exception e) {
            {
            }
         }
      }
      
      private void spendSomeTime() {
         Db4o.configure().readOnly(true);
         ObjectContainer con1 = Db4o.openFile(FILE);
         ObjectSet set1 = con1.get(new ReadOnly());
         while (set1.hasNext()) {
            ReadOnly ro1 = (ReadOnly)set1.next();
            if (ro1.myString.Equals(MY_STRING + "1")) {
               j4o.lang.JavaSystem._out.println("O.K. " + ro1.myString);
            }
            if (ro1.myString.Equals(MY_STRING + (COUNT - 1))) {
               j4o.lang.JavaSystem._out.println("O.K. " + ro1.myString);
            }
            lock (this) {
               try {
                  {
                     j4o.lang.JavaSystem.wait(this, 50);
                  }
               }  catch (Exception e) {
                  {
                  }
               }
            }
         }
         con1.close();
      }
      
      private void checkCount() {
         Db4o.configure().readOnly(true);
         ObjectContainer con1 = Db4o.openFile(FILE);
         int size1 = con1.get(new ReadOnly()).size();
         if (size1 != COUNT) {
            throw new RuntimeException("ReadOnly.test: unexpected number of objects:" + size1);
         }
         con1.close();
      }
   }
}