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
using com.db4o.reflect;
namespace com.db4o.reflect.jdk {

   public class CReflect : IReflect {
      
      public CReflect() : base() {
      }
      private static CReflect reflect = null;
      private IArray i_array = new CArray();
      
      public static CReflect getDefault() {
         if (reflect == null) reflect = new CReflect();
         return reflect;
      }
      
      public IArray array() {
         return i_array;
      }
      
      public bool constructorCallsSupported() {
         return true;
      }
      
      public IClass forName(String xstring) {
         Class var_class1 = Db4o.classForName(xstring);
         if (var_class1 == null) return null;
         return new CClass(var_class1);
      }
      
      public bool methodCallsSupported() {
         return true;
      }
   }
}