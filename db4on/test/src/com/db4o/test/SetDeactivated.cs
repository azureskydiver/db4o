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
         Test.deleteAllInstances(this);
         Test.store(new SetDeactivated("hi"));
         Test.commit();
      }
      
      public void test() {
         SetDeactivated sd1 = (SetDeactivated)Test.getOne(this);
         Test.objectContainer().deactivate(sd1, 1);
         Test.store(sd1);
         Test.objectContainer().purge(sd1);
         sd1 = (SetDeactivated)Test.getOne(this);
         Test.objectContainer().activate(sd1, 1);
         Test.ensure(sd1.foo.Equals("hi"));
      }
   }
}