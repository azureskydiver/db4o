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
         Tester.deleteAllInstances(this);
         name = "hi";
         Tester.store(this);
         for (int i1 = 0; i1 < COUNT; i1++) {
            Tester.store(new StoredClassInformation());
         }
         StoredClass[] storedClasses1 = Tester.objectContainer().ext().storedClasses();
         StoredClass myClass1 = Tester.objectContainer().ext().storedClass(this);
         bool found1 = false;
         for (int i1 = 0; i1 < storedClasses1.Length; i1++) {
            if (storedClasses1[i1].getName().Equals(myClass1.getName())) {
               found1 = true;
               break;
            }
         }
         Tester.ensure(found1);
         long id1 = Tester.objectContainer().getID(this);
         long[] ids1 = myClass1.getIDs();
         Tester.ensure(ids1.Length == COUNT + 1);
         found1 = false;
         for (int i1 = 0; i1 < ids1.Length; i1++) {
            if (ids1[i1] == id1) {
               found1 = true;
               break;
            }
         }
         Tester.ensure(found1);
      }
   }
}