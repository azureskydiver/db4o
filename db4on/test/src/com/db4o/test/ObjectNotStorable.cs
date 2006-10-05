/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

using Db4oTools;

using j4o.lang;
using j4o.io;
using com.db4o;

namespace com.db4o.test {

   public class ObjectNotStorable : Runnable {
      private static String FILE = "notStorable.yap";
      private static bool throwException = false;
      private String name;
      
      internal ObjectNotStorable(String name) : base() {
         if (throwException) {
            throw new Exception();
         }
         this.name = name;
      }
      
      public static void Main(String[] args) {
         throwException = false;
         new ObjectNotStorable(null).Run();
      }
      
      public void Run() {
         new File(FILE).Delete();
         Db4o.Configure().ExceptionsOnNotStorable(true);
         Run1();
      }
      
      private void Run1() {
         try {
            {
               SetExc();
            }
         }  catch (Exception e) {
            {
               j4o.lang.JavaSystem.PrintStackTrace(e);
            }
         }
         try {
            {
               GetExc();
            }
         }  catch (Exception e) {
            {
               j4o.lang.JavaSystem.PrintStackTrace(e);
            }
         }
      }
      
      private static void SetOK() {
         throwException = false;
         ObjectContainer con1 = Db4o.OpenFile(FILE);
         ObjectNotStorable ons1 = new ObjectNotStorable("setOK");
         con1.Set(ons1);
         con1.Close();
      }
      
      private static void SetExc() {
         ObjectContainer con1 = Db4o.OpenFile(FILE);
         throwException = false;
         ObjectNotStorable ons1 = new ObjectNotStorable("setExc");
         throwException = true;
         con1.Set(ons1);
         con1.Close();
      }
      
      private static void GetOK() {
         throwException = false;
         ObjectContainer con1 = Db4o.OpenFile(FILE);
         ObjectSet set1 = con1.Get(new ObjectNotStorable(null));
         while (set1.HasNext()) {
            Logger.Log(con1, set1.Next());
         }
         con1.Close();
      }
      
      private static void GetExc() {
         throwException = true;
         ObjectContainer con1 = Db4o.OpenFile(FILE);
         ObjectSet set1 = con1.Get(null);
         while (set1.HasNext()) {
            Logger.Log(con1, set1.Next());
         }
         con1.Close();
      }
   }
}