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
using com.db4o.ext;
namespace com.db4o.test {

   public class CascadeDeleteArray {
      
      public CascadeDeleteArray() : base() {
      }
      internal ArrayElem[] elements;
      
      public void configure() {
         Db4o.configure().objectClass(this).cascadeOnDelete(true);
      }
      
      public void storeOne() {
        elements = new ArrayElem[]{
            new ArrayElem("one"),
            new ArrayElem("two"),
            new ArrayElem("three")         
        };
      }
      
      public void testOne() {
         Test.ensureOccurrences(typeof(ArrayElem), 3);
         Test.delete(this);
         Test.ensureOccurrences(typeof(ArrayElem), 0);
         Test.rollBack();
         Test.ensureOccurrences(typeof(ArrayElem), 3);
         Test.delete(this);
         Test.ensureOccurrences(typeof(ArrayElem), 0);
         Test.commit();
         Test.ensureOccurrences(typeof(ArrayElem), 0);
      }
      
      public class ArrayElem {
         internal String name;
         
         public ArrayElem() : base() {
         }
         
         public ArrayElem(String name) : base() {
            this.name = name;
         }
      }
   }
}