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
      
      public void configure() {
         Db4o.configure().objectClass(this).cascadeOnUpdate(true);
         Db4o.configure().objectClass(this).cascadeOnDelete(true);
      }
      
      public void store() {
         Tester.deleteAllInstances(this);
         Tester.deleteAllInstances(new Atom());
         Tester.deleteAllInstances(new com.db4o.config.Entry());
         TestHashTable tht1 = new TestHashTable();
         tht1.ht = new Hashtable();
         tht1.ht["t1"] = new Atom("t1");
         tht1.ht["t2"] = new Atom("t2");
         Tester.store(tht1);
      }
      
      public void test() {
         com.db4o.config.Entry checkEntries1 = new com.db4o.config.Entry();
         TestHashTable tht1 = (TestHashTable)Tester.getOne(this);
         Tester.ensure(tht1.ht.Count == 2);
         Tester.ensure(tht1.ht["t1"].Equals(new Atom("t1")));
         Tester.ensure(tht1.ht["t2"].Equals(new Atom("t2")));
         tht1.ht["t2"] = new Atom("t3");
         Tester.store(tht1);
         if (Tester.COMPARE_INTERNAL_OK) {
            Tester.ensureOccurrences(checkEntries1, 2);
            Tester.commit();
            Tester.ensureOccurrences(checkEntries1, 2);
            Tester.deleteAllInstances(this);
            Tester.ensureOccurrences(checkEntries1, 0);
            Tester.rollBack();
            Tester.ensureOccurrences(checkEntries1, 2);
            Tester.deleteAllInstances(this);
            Tester.ensureOccurrences(checkEntries1, 0);
            Tester.commit();
            Tester.ensureOccurrences(checkEntries1, 0);
         }
      }
   }
}