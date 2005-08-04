/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using j4o.lang;
using j4o.util;
namespace com.db4o.test {

   public class KeepCollectionContent {
      
      public KeepCollectionContent() : base() {
      }
      
      public void store() {
         Tester.deleteAllInstances(new Atom());
         Tester.deleteAllInstances(new System.Collections.Hashtable());
         Tester.deleteAllInstances(new ArrayList());
         System.Collections.Hashtable ht1 = new System.Collections.Hashtable();
         ht1.Add(new Atom(), new Atom());
         Tester.store(ht1);
         ArrayList al1 = new ArrayList();
         al1.Add(new Atom());
         Tester.store(al1);
      }
      
      public void test() {
         Tester.deleteAllInstances(new System.Collections.Hashtable());
         Tester.deleteAllInstances(new ArrayList());
         Tester.ensureOccurrences(new Atom(), 3);
      }
   }
}