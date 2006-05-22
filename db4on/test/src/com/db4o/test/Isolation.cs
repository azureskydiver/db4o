/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test {

   public class Isolation {
      
      public Isolation() : base() {
      }
      
      public void Store() {
         Tester.DeleteAllInstances(this);
      }
      
      public void Test() {
         if (Tester.IsClientServer()) {
            ObjectContainer oc1 = Tester.CurrentServer().Ext().ObjectContainer();
            oc1.Set(new Isolation());
            Tester.Ensure(Tester.Occurrences(this) == 0);
            oc1.Commit();
            Tester.Ensure(Tester.Occurrences(this) == 1);
         }
      }
   }
}