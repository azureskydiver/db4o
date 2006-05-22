/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test {

   public class SetDeactivated {
      internal String foo;
      
      public SetDeactivated() : base() {
      }
      
      public SetDeactivated(String foo) : base() {
         this.foo = foo;
      }
      
      public void Store() {
         Tester.DeleteAllInstances(this);
         Tester.Store(new SetDeactivated("hi"));
         Tester.Commit();
      }
      
      public void Test() {
         SetDeactivated sd1 = (SetDeactivated)Tester.GetOne(this);
         Tester.ObjectContainer().Deactivate(sd1, 1);
         Tester.Store(sd1);
         Tester.ObjectContainer().Purge(sd1);
         sd1 = (SetDeactivated)Tester.GetOne(this);
         Tester.ObjectContainer().Activate(sd1, 1);
         Tester.Ensure(sd1.foo.Equals("hi"));
      }
   }
}