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

   internal class YShort : YapJavaClass {
      
      internal YShort() : base() {
      }
      static internal int LENGTH = 2;
      private static Int16 i_primitive = System.Convert.ToInt16((short)0);
      private static Class i_class = j4o.lang.Class.getClassForObject(i_primitive);
      private short i_compareTo;
      
      public override int getID() {
         return 8;
      }
      
      public override Class getJavaClass() {
         return i_class;
      }
      
      public override Class getPrimitiveJavaClass() {
         return Class.getClassForType(typeof(Int16));
      }
      
      public override int linkLength() {
         return 2;
      }
      
      internal override Object primitiveNull() {
         return i_primitive;
      }
      
      internal override Object read1(YapReader yapreader) {
         short i1 = readShort(yapreader);
         return System.Convert.ToInt16(i1);
      }
      
      static internal short readShort(YapReader yapreader) {
         int i1 = 0;
         for (int i_0_1 = 0; i_0_1 < 2; i_0_1++) i1 = (i1 << 8) + (yapreader._buffer[yapreader._offset++] & 255);
         return (short)i1;
      }
      
      public override void write(Object obj, YapWriter yapwriter) {
         writeShort(System.Convert.ToInt16((Int16)obj), yapwriter);
      }
      
      static internal void writeShort(int i, YapWriter yapwriter) {
         for (int i_1_1 = 0; i_1_1 < 2; i_1_1++) yapwriter._buffer[yapwriter._offset++] = (byte)(i >> (1 - i_1_1) * 8);
      }
      
      private short val(Object obj) {
         return System.Convert.ToInt16((Int16)obj);
      }
      
      internal override void prepareComparison1(Object obj) {
         i_compareTo = val(obj);
      }
      
      internal override bool isEqual1(Object obj) {
         return obj is Int16 && val(obj) == i_compareTo;
      }
      
      internal override bool isGreater1(Object obj) {
         return obj is Int16 && val(obj) > i_compareTo;
      }
      
      internal override bool isSmaller1(Object obj) {
         return obj is Int16 && val(obj) < i_compareTo;
      }
   }
}