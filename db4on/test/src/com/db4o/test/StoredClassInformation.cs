/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o.ext;
namespace com.db4o.test {

   public class StoredClassInformation {
      
      public StoredClassInformation() : base() {
      }
      static internal int COUNT = 10;
      public String name;
      
      public void test() {
         Test.deleteAllInstances(this);
         name = "hi";
         Test.store(this);
         for (int i1 = 0; i1 < COUNT; i1++) {
            Test.store(new StoredClassInformation());
         }
         StoredClass[] storedClasses1 = Test.objectContainer().ext().storedClasses();
         StoredClass myClass1 = Test.objectContainer().ext().storedClass(this);
         bool found1 = false;
         for (int i1 = 0; i1 < storedClasses1.Length; i1++) {
            if (storedClasses1[i1].getName().Equals(myClass1.getName())) {
               found1 = true;
               break;
            }
         }
         Test.ensure(found1);
         long id1 = Test.objectContainer().getID(this);
         long[] ids1 = myClass1.getIDs();
         Test.ensure(ids1.Length == COUNT + 1);
         found1 = false;
         for (int i1 = 0; i1 < ids1.Length; i1++) {
            if (ids1[i1] == id1) {
               found1 = true;
               break;
            }
         }
         Test.ensure(found1);
      }
   }
}