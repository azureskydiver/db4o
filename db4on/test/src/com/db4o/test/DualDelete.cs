/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test {

   public class DualDelete {
      
      public DualDelete() : base() {
      }
      internal Atom atom;
      
      public void Configure() {
         Db4o.Configure().ObjectClass(this).CascadeOnDelete(true);
         Db4o.Configure().ObjectClass(this).CascadeOnUpdate(true);
      }
      
      public void Store() {
         Tester.DeleteAllInstances(this);
         Tester.DeleteAllInstances(new Atom());
         Tester.EnsureOccurrences(new Atom(),0);
         DualDelete dd11 = new DualDelete();
         dd11.atom = new Atom("justone");
         Tester.Store(dd11);
         DualDelete dd21 = new DualDelete();
         dd21.atom = dd11.atom;
         Tester.Store(dd21);
      }
      
      public void Test() {
         Tester.EnsureOccurrences(new Atom(), 1);
         Tester.DeleteAllInstances(this);
         Tester.EnsureOccurrences(new Atom(), 0);
         Tester.RollBack();
         Tester.EnsureOccurrences(new Atom(), 1);
         Tester.DeleteAllInstances(this);
         Tester.EnsureOccurrences(new Atom(), 0);
         Tester.Commit();
         Tester.EnsureOccurrences(new Atom(), 0);
         Tester.RollBack();
         Tester.EnsureOccurrences(new Atom(), 0);
      }
   }
}