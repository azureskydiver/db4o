/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test {

   public class DualDelete {
      
      public DualDelete() : base() {
      }
      internal Atom atom;
      
      public void configure() {
         Db4o.configure().objectClass(this).cascadeOnDelete(true);
         Db4o.configure().objectClass(this).cascadeOnUpdate(true);
      }
      
      public void store() {
         Tester.deleteAllInstances(this);
         Tester.deleteAllInstances(new Atom());
         Tester.ensureOccurrences(new Atom(),0);
         DualDelete dd11 = new DualDelete();
         dd11.atom = new Atom("justone");
         Tester.store(dd11);
         DualDelete dd21 = new DualDelete();
         dd21.atom = dd11.atom;
         Tester.store(dd21);
      }
      
      public void test() {
         Tester.ensureOccurrences(new Atom(), 1);
         Tester.deleteAllInstances(this);
         Tester.ensureOccurrences(new Atom(), 0);
         Tester.rollBack();
         Tester.ensureOccurrences(new Atom(), 1);
         Tester.deleteAllInstances(this);
         Tester.ensureOccurrences(new Atom(), 0);
         Tester.commit();
         Tester.ensureOccurrences(new Atom(), 0);
         Tester.rollBack();
         Tester.ensureOccurrences(new Atom(), 0);
      }
   }
}