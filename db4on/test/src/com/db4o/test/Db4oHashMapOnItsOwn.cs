/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using j4o.lang;
using j4o.util;
using com.db4o;
using com.db4o.ext;
namespace com.db4o.test {

   /**
    * 
    */
   public class Db4oHashMapOnItsOwn {
      
      public Db4oHashMapOnItsOwn() : base() {
      }

      internal Object obj;
      
      public void storeOne() {
         ExtObjectContainer oc1 = Tester.objectContainer();
         IDictionary map1 = oc1.collections().newHashMap(10);
         map1["one"] = "one";
         oc1.set(map1);
         obj = map1;
      }
      
      public void testOne() {
         IDictionary map1 = (IDictionary)obj;
         Tester.ensure(map1["one"].Equals("one"));
      }
   }
}