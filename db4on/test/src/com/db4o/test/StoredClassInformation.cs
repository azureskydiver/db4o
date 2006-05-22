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
      
      public void Test() {
         Tester.DeleteAllInstances(this);
         name = "hi";
         Tester.Store(this);
         for (int i1 = 0; i1 < COUNT; i1++) {
            Tester.Store(new StoredClassInformation());
         }
         StoredClass[] storedClasses1 = Tester.ObjectContainer().Ext().StoredClasses();
         StoredClass myClass1 = Tester.ObjectContainer().Ext().StoredClass(this);
         bool found1 = false;
         for (int i1 = 0; i1 < storedClasses1.Length; i1++) {
            if (storedClasses1[i1].GetName().Equals(myClass1.GetName())) {
               found1 = true;
               break;
            }
         }
         Tester.Ensure(found1);
         long id1 = Tester.ObjectContainer().GetID(this);
         long[] ids1 = myClass1.GetIDs();
         Tester.Ensure(ids1.Length == COUNT + 1);
         found1 = false;
         for (int i1 = 0; i1 < ids1.Length; i1++) {
            if (ids1[i1] == id1) {
               found1 = true;
               break;
            }
         }
         Tester.Ensure(found1);
      }
   }
}