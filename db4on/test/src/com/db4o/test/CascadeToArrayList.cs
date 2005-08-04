/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using com.db4o.foundation;
using com.db4o;
namespace com.db4o.test {

   public class CascadeToArrayList {
      
      public CascadeToArrayList() : base() {
      }
      internal ArrayList al;
      
      public void configure() {
         Db4o.configure().objectClass(this).cascadeOnUpdate(true);
         Db4o.configure().objectClass(this).cascadeOnDelete(true);
      }
      
      public void store() {
         Tester.deleteAllInstances(this);
         Tester.deleteAllInstances(new Atom());
         CascadeToArrayList ctal = new CascadeToArrayList();
         ctal.al = new ArrayList();
         ctal.al.Add(new Atom("stored1"));
         ctal.al.Add(new Atom(new Atom("storedChild1"), "stored2"));
         Tester.store(ctal);
      }
      
      public void test() {
         Tester.forEach(this, new VisitorCAL1());
         Tester.reOpen();
         Tester.forEach(this, new VisitorCAL2());
         Tester.reOpen();
         Tester.deleteAllInstances(this);
         Tester.ensureOccurrences(new Atom(), 1);
      }
   }

		public class VisitorCAL1:Visitor4
		{
            public void visit(Object obj) {
               CascadeToArrayList ctal = (CascadeToArrayList)obj;
               IEnumerator i1 = ctal.al.GetEnumerator();
               while (i1.MoveNext()) {
                  Atom atom1 = (Atom)i1.Current;
                  atom1.name = "updated";
                  if (atom1.child != null) {
                     atom1.child.name = "updated";
                  }
               }
               Tester.store(ctal);
            }
		}



		public class VisitorCAL2:Visitor4
		{
             public void visit(Object obj) {
               CascadeToArrayList ctal = (CascadeToArrayList)obj;
               IEnumerator i1 = ctal.al.GetEnumerator();
               while (i1.MoveNext()) {
                  Atom atom1 = (Atom)i1.Current;
                  Tester.ensure(atom1.name.Equals("updated"));
                  if (atom1.child != null) {
                     Tester.ensure(!atom1.child.name.Equals("updated"));
                  }
               }
            }
		}


}