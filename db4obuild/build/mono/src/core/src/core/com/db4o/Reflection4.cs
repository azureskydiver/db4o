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
using j4o.lang.reflect;
namespace com.db4o {

   public class Reflection4 {
      
      public Reflection4() : base() {
      }
      
      static internal Object invoke(Object obj, String xstring, Class[] var_classes, Object[] objs) {
         return invoke(j4o.lang.Class.getClassForObject(obj).getName(), xstring, var_classes, objs, obj);
      }
      
      static internal Object invoke(String xstring, String string_0_, Class[] var_classes, Object[] objs, Object obj) {
         try {
            {
               Method method1 = getMethod(xstring, string_0_, var_classes);
               return method1.invoke(obj, objs);
            }
         }  catch (Exception throwable) {
            {
               return null;
            }
         }
      }
      
      static internal Method getMethod(String xstring, String string_1_, Class[] var_classes) {
         try {
            {
               Class var_class1 = Class.forName(xstring);
               Method method1 = var_class1.getMethod(string_1_, var_classes);
               return method1;
            }
         }  catch (Exception throwable) {
            {
               return null;
            }
         }
      }
   }
}