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

   internal class YChar : YapJavaClass {
      
      internal YChar() : base() {
      }
      static internal int LENGTH = 2;
      private static Char i_primitive = System.Convert.ToChar((char)0);
      private static Class i_class = j4o.lang.Class.getClassForObject(i_primitive);
      private char i_compareTo;
      
      public override int getID() {
         return 7;
      }
      
      public override Class getJavaClass() {
         return i_class;
      }
      
      public override Class getPrimitiveJavaClass() {
         return Class.getClassForType(typeof(Char));
      }
      
      public override int linkLength() {
         return 2;
      }
      
      internal override Object primitiveNull() {
         return i_primitive;
      }
      
      internal override Object read1(YapReader yapreader) {
         int i1 = yapreader.readByte();
         int i_0_1 = yapreader.readByte();
         char c1 = (char)(i1 & 255 | (i_0_1 & 255) << 8);
         return System.Convert.ToChar(c1);
      }
      
      public override void write(Object obj, YapWriter yapwriter) {
         int i1 = System.Convert.ToChar((Char)obj);
         yapwriter.append((byte)(i1 & 255));
         yapwriter.append((byte)(i1 >> 8));
      }
      
      private char val(Object obj) {
         return System.Convert.ToChar((Char)obj);
      }
      
      internal override void prepareComparison1(Object obj) {
         i_compareTo = val(obj);
      }
      
      internal override bool isEqual1(Object obj) {
         return obj is Char && val(obj) == i_compareTo;
      }
      
      internal override bool isGreater1(Object obj) {
         return obj is Char && val(obj) > i_compareTo;
      }
      
      internal override bool isSmaller1(Object obj) {
         return obj is Char && val(obj) < i_compareTo;
      }
   }
}