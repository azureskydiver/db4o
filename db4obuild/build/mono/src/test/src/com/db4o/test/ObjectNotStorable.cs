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
using com.db4o.tools;
namespace com.db4o.test {

   public class ObjectNotStorable : Runnable {
      private static String FILE = "notStorable.yap";
      private static bool throwException = false;
      private String name;
      
      internal ObjectNotStorable(String name) : base() {
         if (throwException) {
            throw new RuntimeException();
         }
         this.name = name;
      }
      
      public static void Main(String[] args) {
         throwException = false;
         new ObjectNotStorable(null).run();
      }
      
      public void run() {
         new File(FILE).delete();
         Db4o.configure().exceptionsOnNotStorable(true);
         run1();
      }
      
      private void run1() {
         try {
            {
               setExc();
            }
         }  catch (Exception e) {
            {
               j4o.lang.JavaSystem.printStackTrace(e);
            }
         }
         try {
            {
               getExc();
            }
         }  catch (Exception e) {
            {
               j4o.lang.JavaSystem.printStackTrace(e);
            }
         }
      }
      
      private static void setOK() {
         throwException = false;
         ObjectContainer con1 = Db4o.openFile(FILE);
         ObjectNotStorable ons1 = new ObjectNotStorable("setOK");
         con1.set(ons1);
         con1.close();
      }
      
      private static void setExc() {
         ObjectContainer con1 = Db4o.openFile(FILE);
         throwException = false;
         ObjectNotStorable ons1 = new ObjectNotStorable("setExc");
         throwException = true;
         con1.set(ons1);
         con1.close();
      }
      
      private static void getOK() {
         throwException = false;
         ObjectContainer con1 = Db4o.openFile(FILE);
         ObjectSet set1 = con1.get(new ObjectNotStorable(null));
         while (set1.hasNext()) {
            Logger.log(con1, set1.next());
         }
         con1.close();
      }
      
      private static void getExc() {
         throwException = true;
         ObjectContainer con1 = Db4o.openFile(FILE);
         ObjectSet set1 = con1.get(null);
         while (set1.hasNext()) {
            Logger.log(con1, set1.next());
         }
         con1.close();
      }
   }
}