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
using com.db4o.ext;
namespace com.db4o.test {

   /**
    * 
    */
   public class Db4oHashMapOnItsOwn {
      
      public Db4oHashMapOnItsOwn() : base() {
      }

      internal Object obj;
      
      public void storeOne() {
         ExtObjectContainer oc1 = Test.objectContainer();
         IDictionary map1 = oc1.collections().newHashMap(10);
         map1["one"] = "one";
         oc1.set(map1);
         obj = map1;
      }
      
      public void testOne() {
         IDictionary map1 = (IDictionary)obj;
         Test.ensure(map1["one"].Equals("one"));
      }
   }
}