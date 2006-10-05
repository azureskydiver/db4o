/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

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
         Db4o.Configure().ReadOnly(true);
         new ReadOnly().SpendSomeTime();
         Db4o.Configure().ReadOnly(false);
      }
      
      public void Run() {
         SetUp();
         Test();
         Db4o.Configure().ReadOnly(false);
      }
      
      private void SetUp() {
         new File(FILE).Delete();
         ObjectContainer con1 = Db4o.OpenFile(FILE);
         for (int i1 = 0; i1 < COUNT; i1++) {
            ReadOnly ro1 = new ReadOnly();
            ro1.myString = MY_STRING + i1;
            con1.Set(ro1);
         }
         con1.Close();
      }
      
      private void Test() {
         Db4o.Configure().ReadOnly(true);
         CheckCount();
         ObjectContainer con1 = Db4o.OpenFile(FILE);
         con1.Set(new ReadOnly());
         con1.Close();
         CheckCount();
         try {
            {
            }
         }  catch (Exception e) {
            {
            }
         }
      }
      
      private void SpendSomeTime() {
         Db4o.Configure().ReadOnly(true);
         ObjectContainer con1 = Db4o.OpenFile(FILE);
         ObjectSet set1 = con1.Get(new ReadOnly());
         while (set1.HasNext()) {
            ReadOnly ro1 = (ReadOnly)set1.Next();
            if (ro1.myString.Equals(MY_STRING + "1")) {
               System.Console.WriteLine("O.K. " + ro1.myString);
            }
            if (ro1.myString.Equals(MY_STRING + (COUNT - 1))) {
				System.Console.WriteLine("O.K. " + ro1.myString);
            }
            lock (this) {
               try {
                  {
                     j4o.lang.JavaSystem.Wait(this, 50);
                  }
               }  catch (Exception e) {
                  {
                  }
               }
            }
         }
         con1.Close();
      }
      
      private void CheckCount() {
         Db4o.Configure().ReadOnly(true);
         ObjectContainer con1 = Db4o.OpenFile(FILE);
         int size1 = con1.Get(new ReadOnly()).Size();
         if (size1 != COUNT) {
            throw new Exception("ReadOnly.test: unexpected number of objects:" + size1);
         }
         con1.Close();
      }
   }
}