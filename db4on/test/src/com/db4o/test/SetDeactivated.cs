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
      
      public void store() {
         Tester.deleteAllInstances(this);
         Tester.store(new SetDeactivated("hi"));
         Tester.commit();
      }
      
      public void test() {
         SetDeactivated sd1 = (SetDeactivated)Tester.getOne(this);
         Tester.objectContainer().deactivate(sd1, 1);
         Tester.store(sd1);
         Tester.objectContainer().purge(sd1);
         sd1 = (SetDeactivated)Tester.getOne(this);
         Tester.objectContainer().activate(sd1, 1);
         Tester.ensure(sd1.foo.Equals("hi"));
      }
   }
}