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
      
      public void Configure() {
         Db4o.Configure().ObjectClass(this).CascadeOnUpdate(true);
         Db4o.Configure().ObjectClass(this).CascadeOnDelete(true);
      }
      
      public void Store() {
         Tester.DeleteAllInstances(this);
         Tester.DeleteAllInstances(new Atom());
         CascadeToArrayList ctal = new CascadeToArrayList();
         ctal.al = new ArrayList();
         ctal.al.Add(new Atom("stored1"));
         ctal.al.Add(new Atom(new Atom("storedChild1"), "stored2"));
         Tester.Store(ctal);
      }
      
      public void Test() {
         Tester.ForEach(this, new VisitorCAL1());
         Tester.ReOpen();
         Tester.ForEach(this, new VisitorCAL2());
         Tester.ReOpen();
         Tester.DeleteAllInstances(this);
         Tester.EnsureOccurrences(new Atom(), 1);
      }
   }

		public class VisitorCAL1:Visitor4
		{
            public void Visit(Object obj) {
               CascadeToArrayList ctal = (CascadeToArrayList)obj;
               IEnumerator i1 = ctal.al.GetEnumerator();
               while (i1.MoveNext()) {
                  Atom atom1 = (Atom)i1.Current;
                  atom1.name = "updated";
                  if (atom1.child != null) {
                     atom1.child.name = "updated";
                  }
               }
               Tester.Store(ctal);
            }
		}



		public class VisitorCAL2:Visitor4
		{
             public void Visit(Object obj) {
               CascadeToArrayList ctal = (CascadeToArrayList)obj;
               IEnumerator i1 = ctal.al.GetEnumerator();
               while (i1.MoveNext()) {
                  Atom atom1 = (Atom)i1.Current;
                  Tester.Ensure(atom1.name.Equals("updated"));
                  if (atom1.child != null) {
                     Tester.Ensure(!atom1.child.name.Equals("updated"));
                  }
               }
            }
		}


}