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
         Test.deleteAllInstances(new Atom());
         Test.deleteAllInstances(new System.Collections.Hashtable());
         Test.deleteAllInstances(new ArrayList());
         System.Collections.Hashtable ht1 = new System.Collections.Hashtable();
         ht1.Add(new Atom(), new Atom());
         Test.store(ht1);
         ArrayList al1 = new ArrayList();
         al1.Add(new Atom());
         Test.store(al1);
      }
      
      public void test() {
         Test.deleteAllInstances(new System.Collections.Hashtable());
         Test.deleteAllInstances(new ArrayList());
         Test.ensureOccurrences(new Atom(), 3);
      }
   }
}