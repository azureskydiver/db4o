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

   public class IsStored {
      
      public IsStored() : base() {
      }
      internal String myString;
      
      public void test() {
         ObjectContainer con = Test.objectContainer();
         Test.deleteAllInstances(this);
         IsStored isStored1 = new IsStored();
         isStored1.myString = "isStored";
         con.set(isStored1);
         Test.ensure(con.ext().isStored(isStored1));
         Test.ensure(Test.occurrences(this) == 1);
         con.delete(isStored1);
         Test.ensure(!con.ext().isStored(isStored1));
         Test.ensure(Test.occurrences(this) == 0);
         con.commit();
         if (con.ext().isStored(isStored1)) {
            if (!Test.clientServer) {
               Test.error();
            }
         }
         Test.ensure(Test.occurrences(this) == 0);
         con.set(isStored1);
         Test.ensure(con.ext().isStored(isStored1));
         Test.ensure(Test.occurrences(this) == 1);
         con.commit();
         Test.ensure(con.ext().isStored(isStored1));
         Test.ensure(Test.occurrences(this) == 1);
         con.delete(isStored1);
         Test.ensure(!con.ext().isStored(isStored1));
         Test.ensure(Test.occurrences(this) == 0);
         con.commit();
         if (con.ext().isStored(isStored1)) {
            if (!Test.clientServer) {
               Test.error();
            }
         }
         Test.ensure(Test.occurrences(this) == 0);
      }
   }
}