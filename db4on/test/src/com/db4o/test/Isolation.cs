/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test {

   public class Isolation {
      
      public Isolation() : base() {
      }
      
      public void store() {
         Test.deleteAllInstances(this);
      }
      
      public void test() {
         if (Test.isClientServer()) {
            ObjectContainer oc1 = Test.currentServer().ext().objectContainer();
            oc1.set(new Isolation());
            Test.ensure(Test.occurrences(this) == 0);
            oc1.commit();
            Test.ensure(Test.occurrences(this) == 1);
         }
      }
   }
}