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
using j4o.lang;
using com.db4o;
using com.db4o.query;
namespace com.db4o.test {

   public class CascadeOnActivate {
      
      public CascadeOnActivate() : base() {
      }
      public String name;
      public CascadeOnActivate child;
      
      public void configure() {
         Db4o.configure().objectClass(this).cascadeOnActivate(true);
      }
      
      public void store() {
         name = "1";
         child = new CascadeOnActivate();
         child.name = "2";
         child.child = new CascadeOnActivate();
         child.child.name = "3";
         Test.store(this);
      }
      
      public void test() {
         Query q1 = Test.query();
         q1.constrain(j4o.lang.Class.getClassForObject(this));
         q1.descend("name").constrain("1");
         ObjectSet os1 = q1.execute();
         CascadeOnActivate coa1 = (CascadeOnActivate)os1.next();
         CascadeOnActivate coa31 = coa1.child.child;
         Test.ensure(coa31.name.Equals("3"));
         Test.objectContainer().deactivate(coa1, Int32.MaxValue);
         Test.ensure(coa31.name == null);
         Test.objectContainer().activate(coa1, 1);
         Test.ensure(coa31.name.Equals("3"));
      }
   }
}