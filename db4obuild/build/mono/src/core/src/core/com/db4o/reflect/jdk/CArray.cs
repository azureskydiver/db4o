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
using com.db4o.reflect;
namespace com.db4o.reflect.jdk {

   public class CArray : IArray {
      
      public CArray() : base() {
      }
      
      public Object get(Object obj, int i) {
         return j4o.lang.reflect.JavaArray.get(obj, i);
      }
      
      public int getLength(Object obj) {
         return j4o.lang.reflect.JavaArray.getLength(obj);
      }
      
      public Object newInstance(Class var_class, int i) {
         return j4o.lang.reflect.JavaArray.newInstance(var_class, i);
      }
      
      public Object newInstance(Class var_class, int[] xis) {
         return j4o.lang.reflect.JavaArray.newInstance(var_class, xis);
      }
      
      public void set(Object obj, int i, Object obj_0_) {
         j4o.lang.reflect.JavaArray.set(obj, i, obj_0_);
      }
   }
}