/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using j4o.lang;
using j4o.util;
using com.db4o;
namespace com.db4o.test {

   public class TestHashTable {
      
      public TestHashTable() : base() {
      }
      internal Hashtable ht;
      
      public void Configure() {
         Db4o.Configure().ObjectClass(this).CascadeOnUpdate(true);
         Db4o.Configure().ObjectClass(this).CascadeOnDelete(true);
      }
      
      public void Store() {
         Tester.DeleteAllInstances(this);
         Tester.DeleteAllInstances(new Atom());
         Tester.DeleteAllInstances(new com.db4o.config.Entry());
         TestHashTable tht1 = new TestHashTable();
         tht1.ht = new Hashtable();
         tht1.ht["t1"] = new Atom("t1");
         tht1.ht["t2"] = new Atom("t2");
         Tester.Store(tht1);
      }
      
      public void Test() {
         com.db4o.config.Entry checkEntries1 = new com.db4o.config.Entry();
         TestHashTable tht1 = (TestHashTable)Tester.GetOne(this);
         Tester.Ensure(tht1.ht.Count == 2);
         Tester.Ensure(tht1.ht["t1"].Equals(new Atom("t1")));
         Tester.Ensure(tht1.ht["t2"].Equals(new Atom("t2")));
         tht1.ht["t2"] = new Atom("t3");
         Tester.Store(tht1);
         if (Tester.COMPARE_INTERNAL_OK) {
            Tester.EnsureOccurrences(checkEntries1, 2);
            Tester.Commit();
            Tester.EnsureOccurrences(checkEntries1, 2);
            Tester.DeleteAllInstances(this);
            Tester.EnsureOccurrences(checkEntries1, 0);
            Tester.RollBack();
            Tester.EnsureOccurrences(checkEntries1, 2);
            Tester.DeleteAllInstances(this);
            Tester.EnsureOccurrences(checkEntries1, 0);
            Tester.Commit();
            Tester.EnsureOccurrences(checkEntries1, 0);
         }
      }
   }
}