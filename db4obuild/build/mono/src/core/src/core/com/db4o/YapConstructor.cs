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
using com.db4o.reflect.jdk;
namespace com.db4o {

   internal class YapConstructor {
      private Class i_javaClass;
      private IClass i_reflectorClass;
      private EventDispatcher i_eventDispatcher;
      private IConstructor i_constructor;
      private Object[] i_params;
      internal bool i_dontCallConstructors;
      
      internal YapConstructor(YapStream yapstream, Class var_class, IConstructor iconstructor, Object[] objs, bool xbool, bool bool_0_) : base() {
         i_javaClass = var_class;
         i_constructor = iconstructor;
         i_params = objs;
         i_dontCallConstructors = bool_0_;
         try {
            {
               if (yapstream == null || yapstream.i_config.i_reflect is CReflect) i_reflectorClass = new CClass(var_class); else i_reflectorClass = yapstream.i_config.i_reflect.forName(var_class.getName());
            }
         }  catch (ClassNotFoundException classnotfoundexception) {
            {
            }
         }
         i_eventDispatcher = xbool ? EventDispatcher.forClass(yapstream, i_reflectorClass) : null;
      }
      
      internal bool dispatch(YapStream yapstream, Object obj, int i) {
         if (i_eventDispatcher != null) return i_eventDispatcher.dispatch(yapstream, obj, i);
         return true;
      }
      
      internal String getName() {
         return i_javaClass.getName();
      }
      
      internal Class javaClass() {
         return i_javaClass;
      }
      
      internal Object newInstance() {
         if (i_constructor == null) return i_reflectorClass.newInstance();
         return i_constructor.newInstance(i_params);
      }
      
      internal IClass reflectorClass() {
         return i_reflectorClass;
      }
   }
}