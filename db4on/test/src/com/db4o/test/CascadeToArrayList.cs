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
         Test.deleteAllInstances(this);
         Test.deleteAllInstances(new Atom());
         CascadeToArrayList ctal = new CascadeToArrayList();
         ctal.al = new ArrayList();
         ctal.al.Add(new Atom("stored1"));
         ctal.al.Add(new Atom(new Atom("storedChild1"), "stored2"));
         Test.store(ctal);
      }
      
      public void test() {
         Test.forEach(this, new VisitorCAL1());
         Test.reOpen();
         Test.forEach(this, new VisitorCAL2());
         Test.reOpen();
         Test.deleteAllInstances(this);
         Test.ensureOccurrences(new Atom(), 1);
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
               Test.store(ctal);
            }
		}



		public class VisitorCAL2:Visitor4
		{
             public void visit(Object obj) {
               CascadeToArrayList ctal = (CascadeToArrayList)obj;
               IEnumerator i1 = ctal.al.GetEnumerator();
               while (i1.MoveNext()) {
                  Atom atom1 = (Atom)i1.Current;
                  Test.ensure(atom1.name.Equals("updated"));
                  if (atom1.child != null) {
                     Test.ensure(!atom1.child.name.Equals("updated"));
                  }
               }
            }
		}


}