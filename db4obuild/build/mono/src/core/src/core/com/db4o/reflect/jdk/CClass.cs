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
using com.db4o.reflect;
namespace com.db4o.reflect.jdk {

   public class CClass : IClass {
      private Class clazz;
      
      public CClass(Class var_class) : base() {
         if (var_class == null) throw new ClassNotFoundException();
         clazz = var_class;
      }
      
      public IConstructor[] getDeclaredConstructors() {
         Constructor[] constructors1 = clazz.getDeclaredConstructors();
         IConstructor[] iconstructors1 = new IConstructor[constructors1.Length];
         for (int i1 = 0; i1 < constructors1.Length; i1++) iconstructors1[i1] = new CConstructor(constructors1[i1]);
         return iconstructors1;
      }
      
      public IField getDeclaredField(String xstring) {
         try {
            {
               return new CField(clazz.getDeclaredField(xstring));
            }
         }  catch (Exception exception) {
            {
               return null;
            }
         }
      }
      
      public IField[] getDeclaredFields() {
         Field[] fields1 = clazz.getDeclaredFields();
         IField[] ifields1 = new IField[fields1.Length];
         for (int i1 = 0; i1 < ifields1.Length; i1++) ifields1[i1] = new CField(fields1[i1]);
         return ifields1;
      }
      
      public IMethod getMethod(String xstring, Class[] var_classes) {
         try {
            {
               Method method1 = clazz.getMethod(xstring, var_classes);
               if (method1 == null) return null;
               return new CMethod(method1);
            }
         }  catch (Exception exception) {
            {
               return null;
            }
         }
      }
      
      public bool isAbstract() {
         return Modifier.isAbstract(clazz.getModifiers());
      }
      
      public bool isInterface() {
         return clazz.isInterface();
      }
      
      public Object newInstance() {
         try {
            {
               return clazz.newInstance();
            }
         }  catch (Exception throwable) {
            {
               return null;
            }
         }
      }
   }
}