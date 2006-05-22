/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.test.types;
namespace com.db4o.test {

   public class GarbageCollection {
      
      public GarbageCollection() : base() {
      }
      
      public static void Main(String[] args) {
         new j4o.io.File("tgc.yap").Delete();
         int strSize1 = 1;
         int objectCount1 = 10000;
         ObjectContainer con = Db4o.OpenFile("tgc.yap");
         String longString1 = "String";
         ObjectSimplePublic osp1 = null;
         ArrayTypedPublic atp1 = null;
         for (int i1 = 0; i1 < strSize1; i1++) {
            longString1 = longString1 + longString1;
         }
         int toGetTen1 = objectCount1 / 10;
         for (int i1 = 0; i1 < objectCount1; i1++) {
            atp1 = new ArrayTypedPublic();
            atp1.Set(1);
            con.Set(atp1);
            if ((double)i1 / toGetTen1 - i1 / toGetTen1 < 1.0E-6) {
               con.Commit();
               con.Ext().Purge();
               Mem();
            }
         }
         con.Commit();
         con.Ext().Purge();
         longString1 = null;
         osp1 = null;
         Mem();
         Mem();
         con.Close();
      }
      
      static internal void Mem() {

          // TODO: look for equivalen for .NET

//         j4o.lang.JavaSystem.RunFinalization();
//         Runtime r1 = Runtime.GetRuntime();
//         r1.Gc();
//         r1.RunFinalization();
//         r1.Gc();
//         Console.WriteLine(r1.TotalMemory() - r1.FreeMemory());
      }
   }
}