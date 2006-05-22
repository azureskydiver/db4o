/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test {

   public class SharedObjectContainer {
      
      public SharedObjectContainer() : base() {
      }
      internal String name;
      
      public void StoreOne() {
         name = "hi";
      }
      
      public void TestOne() {
         if (!Tester.IsClientServer()) {
            for (int i1 = 0; i1 < 30; i1++) {
               ObjectContainer con1 = Db4o.OpenFile(Tester.FILE_SOLO);
               Object obj1 = con1.Get(new SharedObjectContainer()).Next();
               Tester.Ensure(obj1 == this);
               con1.Close();
            }
            Tester.Ensure(!Tester.ObjectContainer().Ext().IsClosed());
         }
      }
   }
}