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
using com.db4o;
using com.db4o.reflect;
namespace com.db4o.reflect.jdk {

   public class CConstructor : IConstructor {
      private Constructor constructor;
      
      public CConstructor(Constructor constructor) : base() {
         this.constructor = constructor;
      }
      
      public Class[] getParameterTypes() {
         return constructor.getParameterTypes();
      }
      
      public void setAccessible() {
         Platform.setAccessible(constructor);
      }
      
      public Object newInstance(Object[] objs) {
         try {
            {
               Object obj1 = constructor.newInstance(objs);
               return obj1;
            }
         }  catch (Exception exception) {
            {
               return null;
            }
         }
      }
   }
}