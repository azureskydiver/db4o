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

   public class CField : IField {
      private Field field;
      
      public CField(Field field) : base() {
         this.field = field;
      }
      
      public String getName() {
         return field.getName();
      }
      
      public Class getType() {
         return field.getType();
      }
      
      public bool isPublic() {
         return Modifier.isPublic(field.getModifiers());
      }
      
      public bool isStatic() {
         return Modifier.isStatic(field.getModifiers());
      }
      
      public bool isTransient() {
         return Modifier.isTransient(field.getModifiers());
      }
      
      public void setAccessible() {
         Platform.setAccessible(field);
      }
      
      public Object get(Object obj) {
         try {
            {
               return field.get(obj);
            }
         }  catch (Exception exception) {
            {
               return null;
            }
         }
      }
      
      public void set(Object obj, Object obj_0_) {
         try {
            {
               field.set(obj, obj_0_);
            }
         }  catch (Exception exception) {
            {
            }
         }
      }
   }
}