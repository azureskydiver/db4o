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

   internal class YFloat : YInt {
      
      internal YFloat() : base() {
      }
      private static Single i_primitive = System.Convert.ToSingle(0.0F);
      private static Class i_class = j4o.lang.Class.getClassForObject(i_primitive);
      private float i_compareTo;
      
      public override int getID() {
         return 3;
      }
      
      public override Class getJavaClass() {
         return i_class;
      }
      
      public override Class getPrimitiveJavaClass() {
         return Class.getClassForType(typeof(Single));
      }
      
      internal override Object primitiveNull() {
         return i_primitive;
      }
      
      internal override Object read1(YapReader yapreader) {
         int i1 = YInt.readInt(yapreader);
         return System.Convert.ToSingle(j4o.lang.JavaSystem.intBitsToFloat(i1));
      }
      
      public override void write(Object obj, YapWriter yapwriter) {
         YInt.writeInt(j4o.lang.JavaSystem.floatToIntBits(System.Convert.ToSingle((Single)obj)), yapwriter);
      }
      
      private float valu(Object obj) {
         return System.Convert.ToSingle((Single)obj);
      }
      
      internal override void prepareComparison1(Object obj) {
         i_compareTo = valu(obj);
      }
      
      internal override bool isEqual1(Object obj) {
         return obj is Single && valu(obj) == i_compareTo;
      }
      
      internal override bool isGreater1(Object obj) {
         return obj is Single && valu(obj) > i_compareTo;
      }
      
      internal override bool isSmaller1(Object obj) {
         return obj is Single && valu(obj) < i_compareTo;
      }
   }
}