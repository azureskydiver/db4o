/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test {

   public class Isolation {
      
      public Isolation() : base() {
      }
      
      public void store() {
         Tester.deleteAllInstances(this);
      }
      
      public void test() {
         if (Tester.isClientServer()) {
            ObjectContainer oc1 = Tester.currentServer().ext().objectContainer();
            oc1.set(new Isolation());
            Tester.ensure(Tester.occurrences(this) == 0);
            oc1.commit();
            Tester.ensure(Tester.occurrences(this) == 1);
         }
      }
   }
}