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

   internal class YBoolean : YapJavaClass {
      
      internal YBoolean() : base() {
      }
      static internal int LENGTH = 1;
      private static byte TRUE = 84;
      private static byte FALSE = 70;
      private static byte NULL = 78;
      private static Boolean i_primitive = System.Convert.ToBoolean(false);
      private static Class i_class = j4o.lang.Class.getClassForObject(i_primitive);
      private bool i_compareTo;
      
      public override int getID() {
         return 4;
      }
      
      public override Class getJavaClass() {
         return i_class;
      }
      
      public override Class getPrimitiveJavaClass() {
         return Class.getClassForType(typeof(Boolean));
      }
      
      public override int linkLength() {
         return 1;
      }
      
      internal override Object primitiveNull() {
         return i_primitive;
      }
      
      internal override Object read1(YapReader yapreader) {
         byte i1 = yapreader.readByte();
         if (i1 == 84) return System.Convert.ToBoolean(true);
         if (i1 == 70) return System.Convert.ToBoolean(false);
         return null;
      }
      
      public override void write(Object obj, YapWriter yapwriter) {
         byte i1;
         if (obj == null) i1 = (byte)78; else if (System.Convert.ToBoolean((Boolean)obj)) i1 = (byte)84; else i1 = (byte)70;
         yapwriter.append(i1);
      }
      
      private bool val(Object obj) {
         return System.Convert.ToBoolean((Boolean)obj);
      }
      
      internal override void prepareComparison1(Object obj) {
         i_compareTo = val(obj);
      }
      
      internal override bool isEqual1(Object obj) {
         return obj is Boolean && val(obj) == i_compareTo;
      }
      
      internal override bool isGreater1(Object obj) {
         if (i_compareTo) return false;
         return obj is Boolean && val(obj);
      }
      
      internal override bool isSmaller1(Object obj) {
         if (!i_compareTo) return false;
         return obj is Boolean && !val(obj);
      }
   }
}