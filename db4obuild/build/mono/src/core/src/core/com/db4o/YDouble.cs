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

   internal class YDouble : YLong {
      
      internal YDouble() : base() {
      }
      private static Double i_primitive = System.Convert.ToDouble(0.0);
      private static Class i_class = j4o.lang.Class.getClassForObject(i_primitive);
      private double i_compareToDouble;
      
      public override int getID() {
         return 5;
      }
      
      public override Class getJavaClass() {
         return i_class;
      }
      
      public override Class getPrimitiveJavaClass() {
         return Class.getClassForType(typeof(Double));
      }
      
      internal override Object primitiveNull() {
         return i_primitive;
      }
      
      internal override Object read1(YapReader yapreader) {
         long l1 = YLong.readLong(yapreader);
         return System.Convert.ToDouble(Platform.longToDouble(l1));
      }
      
      public override void write(Object obj, YapWriter yapwriter) {
         YLong.writeLong(Platform.doubleToLong(System.Convert.ToDouble((Double)obj)), yapwriter);
      }
      
      private double dval(Object obj) {
         return System.Convert.ToDouble((Double)obj);
      }
      
      internal override void prepareComparison1(Object obj) {
         i_compareToDouble = dval(obj);
      }
      
      internal override bool isEqual1(Object obj) {
         return obj is Double && dval(obj) == i_compareToDouble;
      }
      
      internal override bool isGreater1(Object obj) {
         return obj is Double && dval(obj) > i_compareToDouble;
      }
      
      internal override bool isSmaller1(Object obj) {
         return obj is Double && dval(obj) < i_compareToDouble;
      }
   }
}