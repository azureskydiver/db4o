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
namespace com.db4o.test {

   public class CreateIndex {
      public String name;
      
      public CreateIndex() : base() {
      }
      
      public CreateIndex(String name) : base() {
         this.name = name;
      }
      
      public void configure() {
         Db4o.configure().objectClass(Class.getClassForType(typeof(CreateIndex))).objectField("name").indexed(true);
      }
      
      public void store() {
         Test.deleteAllInstances(this);
         Test.store(new CreateIndex("a"));
         Test.store(new CreateIndex("c"));
         Test.store(new CreateIndex("b"));
         Object obj11 = Db4o.configure().objectClass(Class.getClassForType(typeof(CreateIndex)));
         Object obj21 = Test.objectContainer().ext().configure().objectClass(Class.getClassForType(typeof(CreateIndex)));
         int xxx1 = 1;
      }
   }
}