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
namespace com.db4o {

   internal class YapHandlers__1 : Visitor4 {
      private YapConstructor[] val__foundConstructor;
      private YapStream val__a_stream;
      private Class val__a_class;
      private YapHandlers stathis0;
      
      internal YapHandlers__1(YapHandlers yaphandlers, YapConstructor[] yapconstructors, YapStream yapstream, Class var_class) : base() {
         stathis0 = yaphandlers;
         val__foundConstructor = yapconstructors;
         val__a_stream = yapstream;
         val__a_class = var_class;
      }
      
      public void visit(Object obj) {
         if (val__foundConstructor[0] == null) {
            IConstructor iconstructor1 = (IConstructor)((TreeIntObject)obj).i_object;
            try {
               {
                  Class[] var_classes1 = iconstructor1.getParameterTypes();
                  Object[] objs1 = new Object[var_classes1.Length];
                  for (int i1 = 0; i1 < objs1.Length; i1++) {
                     for (int i_0_1 = 0; i_0_1 < 8; i_0_1++) {
                        if (var_classes1[i1] == YapHandlers.access__000(stathis0)[i_0_1].getPrimitiveJavaClass()) {
                           objs1[i1] = ((YapJavaClass)YapHandlers.access__000(stathis0)[i_0_1]).primitiveNull();
                           break;
                        }
                     }
                  }
                  Object obj_1_1 = iconstructor1.newInstance(objs1);
                  if (obj_1_1 != null) val__foundConstructor[0] = new YapConstructor(val__a_stream, val__a_class, iconstructor1, objs1, true, false);
               }
            }  catch (Exception throwable) {
               {
               }
            }
         }
      }
   }
}