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
namespace com.db4o {

   internal class YInt : YapJavaClass {
      
      internal YInt() : base() {
      }
      private static Int32 i_primitive = System.Convert.ToInt32(0);
      private static Class i_class = j4o.lang.Class.getClassForObject(i_primitive);
      private int i_compareTo;
      
      public override int getID() {
         return 1;
      }
      
      public override Class getJavaClass() {
         return i_class;
      }
      
      public override Class getPrimitiveJavaClass() {
         return Class.getClassForType(typeof(Int32));
      }
      
      public override int linkLength() {
         return 4;
      }
      
      internal override Object primitiveNull() {
         return i_primitive;
      }
      
      internal override Object read1(YapReader yapreader) {
         int i1 = readInt(yapreader);
         return System.Convert.ToInt32(i1);
      }
      
      static internal int readInt(YapReader yapreader) {
         return yapreader.readInt();
      }
      
      public override void write(Object obj, YapWriter yapwriter) {
         writeInt(System.Convert.ToInt32((Int32)obj), yapwriter);
      }
      
      static internal void writeInt(int i, YapReader yapreader) {
         yapreader.writeInt(i);
      }
      
      private int val(Object obj) {
         return System.Convert.ToInt32((Int32)obj);
      }
      
      internal override void prepareComparison1(Object obj) {
         i_compareTo = val(obj);
      }
      
      internal override bool isEqual1(Object obj) {
         return obj is Int32 && val(obj) == i_compareTo;
      }
      
      internal override bool isGreater1(Object obj) {
         return obj is Int32 && val(obj) > i_compareTo;
      }
      
      internal override bool isSmaller1(Object obj) {
         return obj is Int32 && val(obj) < i_compareTo;
      }
      
      private static Object prop(Object obj, String xstring) {
         try {
            {
               Class[] var_classes1 = {
                  Class.forName("System.Object"),
Class.forName("System.String")               };
               Object[] objs1 = {
                  obj,
xstring               };
               return Reflection4.invoke("s4o.Dynamic", "GetProperty", var_classes1, objs1, null);
            }
         }  catch (Exception exception) {
            {
               return null;
            }
         }
      }
   }
}