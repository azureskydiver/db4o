/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;

using j4o.lang;
using j4o.util;
namespace com.db4o.test {

   public class ExtendsHashTable: System.Collections.Hashtable {
      
      public ExtendsHashTable() : base() {
      }
      
      public void Store() {
         Tester.DeleteAllInstances(this);
         Add(System.Convert.ToInt32(1), "one");
         Add(System.Convert.ToInt32(2), "two");
         Add(System.Convert.ToInt32(3), "three");
         Tester.Store(this);
      }
      
      public void Test() {
         ExtendsHashTable ehm1 = (ExtendsHashTable)Tester.GetOne(this);
         Tester.Ensure(ehm1[System.Convert.ToInt32(1)].Equals("one"));
         Tester.Ensure(ehm1[System.Convert.ToInt32(3)].Equals("three"));
      }
   }
}