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

   internal class YLong : YapJavaClass {
      
      internal YLong() : base() {
      }
      private static Int64 i_primitive = System.Convert.ToInt64(0L);
      private static Class i_class = j4o.lang.Class.getClassForObject(i_primitive);
      protected long i_compareTo;
      
      static internal long decipher(String xstring) {
         String string_0_1 = "";
         for (int i1 = 0; i1 < j4o.lang.JavaSystem.getLengthOf(xstring); i1 += 2) {
            char[] cs1 = new char[2];
            cs1[0] = j4o.lang.JavaSystem.getCharAt(xstring, i1);
            cs1[1] = j4o.lang.JavaSystem.getCharAt(xstring, i1 + 1);
            String string_1_1 = new String(cs1);
            string_0_1 += (char)System.Convert.ToInt32(System.Convert.ToInt32(string_1_1));
         }
         return System.Convert.ToInt64(System.Convert.ToInt64(string_0_1));
      }
      
      public override int getID() {
         return 2;
      }
      
      public override Class getJavaClass() {
         return i_class;
      }
      
      public override Class getPrimitiveJavaClass() {
         return Class.getClassForType(typeof(Int64));
      }
      
      public override int linkLength() {
         return 8;
      }
      
      internal override Object primitiveNull() {
         return i_primitive;
      }
      
      internal override Object read1(YapReader yapreader) {
         long l1 = readLong(yapreader);
         return System.Convert.ToInt64(l1);
      }
      
      static internal long readLong(YapReader yapreader) {
         long l1 = 0L;
         for (int i1 = 0; i1 < 8; i1++) l1 = (l1 << 8) + (long)(yapreader._buffer[yapreader._offset++] & 255);
         return l1;
      }
      
      public override void write(Object obj, YapWriter yapwriter) {
         writeLong(System.Convert.ToInt64((Int64)obj), yapwriter);
      }
      
      static internal void writeLong(long l, YapWriter yapwriter) {
         for (int i1 = 0; i1 < 8; i1++) yapwriter._buffer[yapwriter._offset++] = (byte)(int)(l >> (7 - i1) * 8);
      }
      
      static internal void writeLong(long l, byte[] xis) {
         for (int i1 = 0; i1 < 8; i1++) xis[i1] = (byte)(int)(l >> (7 - i1) * 8);
      }
      
      static internal long readLong(YapWriter yapwriter) {
         long l1 = 0L;
         for (int i1 = 0; i1 < 8; i1++) l1 = (l1 << 8) + (long)(yapwriter._buffer[yapwriter._offset++] & 255);
         return l1;
      }
      
      internal virtual long val(Object obj) {
         return System.Convert.ToInt64((Int64)obj);
      }
      
      internal override void prepareComparison1(Object obj) {
         i_compareTo = val(obj);
      }
      
      internal override bool isEqual1(Object obj) {
         return obj is Int64 && val(obj) == i_compareTo;
      }
      
      internal override bool isGreater1(Object obj) {
         return obj is Int64 && val(obj) > i_compareTo;
      }
      
      internal override bool isSmaller1(Object obj) {
         return obj is Int64 && val(obj) < i_compareTo;
      }
   }
}