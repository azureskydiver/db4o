/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using j4o.lang;
using j4o.util;
namespace com.db4o.test {

   public class KeepCollectionContent {
      
      public KeepCollectionContent() : base() {
      }
      
      public void Store() {
         Tester.DeleteAllInstances(new Atom());
         Tester.DeleteAllInstances(new System.Collections.Hashtable());
         Tester.DeleteAllInstances(new ArrayList());
         System.Collections.Hashtable ht1 = new System.Collections.Hashtable();
         ht1.Add(new Atom(), new Atom());
         Tester.Store(ht1);
         ArrayList al1 = new ArrayList();
         al1.Add(new Atom());
         Tester.Store(al1);
      }
      
      public void Test() {
         Tester.DeleteAllInstances(new System.Collections.Hashtable());
         Tester.DeleteAllInstances(new ArrayList());
         Tester.EnsureOccurrences(new Atom(), 3);
      }
   }
}