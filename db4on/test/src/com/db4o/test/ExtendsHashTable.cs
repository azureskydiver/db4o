/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;

using j4o.lang;
using j4o.util;
namespace com.db4o.test {

   public class ExtendsHashTable: System.Collections.Hashtable {
      
      public ExtendsHashTable() : base() {
      }
      
      public void store() {
         Test.deleteAllInstances(this);
         Add(System.Convert.ToInt32(1), "one");
         Add(System.Convert.ToInt32(2), "two");
         Add(System.Convert.ToInt32(3), "three");
         Test.store(this);
      }
      
      public void test() {
         ExtendsHashTable ehm1 = (ExtendsHashTable)Test.getOne(this);
         Test.ensure(ehm1[System.Convert.ToInt32(1)].Equals("one"));
         Test.ensure(ehm1[System.Convert.ToInt32(3)].Equals("three"));
      }
   }
}