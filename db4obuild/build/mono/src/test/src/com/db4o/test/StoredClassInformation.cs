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
using com.db4o.ext;
namespace com.db4o.test {

   public class StoredClassInformation {
      
      public StoredClassInformation() : base() {
      }
      static internal int COUNT = 10;
      public String name;
      
      public void test() {
         Test.deleteAllInstances(this);
         name = "hi";
         Test.store(this);
         for (int i1 = 0; i1 < COUNT; i1++) {
            Test.store(new StoredClassInformation());
         }
         StoredClass[] storedClasses1 = Test.objectContainer().ext().storedClasses();
         StoredClass myClass1 = Test.objectContainer().ext().storedClass(this);
         bool found1 = false;
         for (int i1 = 0; i1 < storedClasses1.Length; i1++) {
            if (storedClasses1[i1].getName().Equals(myClass1.getName())) {
               found1 = true;
               break;
            }
         }
         Test.ensure(found1);
         long id1 = Test.objectContainer().getID(this);
         long[] ids1 = myClass1.getIDs();
         Test.ensure(ids1.Length == COUNT + 1);
         found1 = false;
         for (int i1 = 0; i1 < ids1.Length; i1++) {
            if (ids1[i1] == id1) {
               found1 = true;
               break;
            }
         }
         Test.ensure(found1);
      }
   }
}