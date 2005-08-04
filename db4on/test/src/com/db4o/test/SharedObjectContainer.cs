/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test {

   public class SharedObjectContainer {
      
      public SharedObjectContainer() : base() {
      }
      internal String name;
      
      public void storeOne() {
         name = "hi";
      }
      
      public void testOne() {
         if (!Tester.isClientServer()) {
            for (int i1 = 0; i1 < 30; i1++) {
               ObjectContainer con1 = Db4o.openFile(Tester.FILE_SOLO);
               Object obj1 = con1.get(new SharedObjectContainer()).next();
               Tester.ensure(obj1 == this);
               con1.close();
            }
            Tester.ensure(!Tester.objectContainer().ext().isClosed());
         }
      }
   }
}