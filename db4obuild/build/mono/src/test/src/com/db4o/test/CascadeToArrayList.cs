/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

using System;
using System.Collections;
using j4o.lang;
using j4o.util;
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