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
         if (!Test.isClientServer()) {
            for (int i1 = 0; i1 < 30; i1++) {
               ObjectContainer con1 = Db4o.openFile(Test.FILE_SOLO);
               Object obj1 = con1.get(new SharedObjectContainer()).next();
               Test.ensure(obj1 == this);
               con1.close();
            }
            Test.ensure(!Test.objectContainer().ext().isClosed());
         }
      }
   }
}