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
using com.db4o.config;
namespace com.db4o.test {

   public class Rename {
      
      public Rename() : base() {
      }
      
      public void test() {
         if (Test.run == 1  && ! Test.isClientServer()) {
            Test.deleteAllInstances(Class.getClassForType(typeof(One)));
            Test.store(new One("wasOne"));
            Test.ensureOccurrences(Class.getClassForType(typeof(One)), 1);
            Test.commit();
            ObjectClass oc1 = Db4o.configure().objectClass(Class.getClassForType(typeof(One)));
            oc1.objectField("nameOne").rename("nameTwo");
            oc1.rename(Class.getClassForType(typeof(Two)).getName());
            Test.reOpenServer();
            Test.ensureOccurrences(Class.getClassForType(typeof(Two)), 1);
            Test.ensureOccurrences(Class.getClassForType(typeof(One)), 0);
            Two two1 = (Two)Test.getOne(Class.getClassForType(typeof(Two)));
            Test.ensure(two1.nameTwo.Equals("wasOne"));
         }
      }
      
      public class One {
         public String nameOne;
         
         public One() : base() {
         }
         
         public One(String name) : base() {
            nameOne = name;
         }
      }
      
      public class Two {
         
         public Two() : base() {
         }
         public String nameTwo;
      }
   }
}