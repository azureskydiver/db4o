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

   abstract internal class YapTypeAbstract : YapJavaClass, YapType {
      
      internal YapTypeAbstract() : base() {
      }
      private Class i_cachedClass;
      private int i_linkLength;
      private Object i_compareTo;
      
      public override Class getJavaClass() {
         return i_cachedClass;
      }
      
      public abstract Object defaultValue();
      
      public abstract int typeID();
      
      public abstract void write(Object obj, byte[] xis, int i);
      
      public abstract Object read(byte[] xis, int i);
      
      public abstract int compare(Object obj, Object obj_0_);
      
      public abstract bool isEqual(Object obj, Object obj_1_);
      
      internal void initialize() {
         i_cachedClass = j4o.lang.Class.getClassForObject(primitiveNull());
         byte[] xis1 = new byte[65];
         for (int i1 = 0; i1 < xis1.Length; i1++) xis1[i1] = (byte)55;
         write(primitiveNull(), xis1, 0);
         for (int i1 = 0; i1 < xis1.Length; i1++) {
            if (xis1[i1] == 55) {
               i_linkLength = i1;
               break;
            }
         }
      }
      
      internal override Object primitiveNull() {
         return defaultValue();
      }
      
      public override void write(Object obj, YapWriter yapwriter) {
         int i1 = yapwriter._offset;
         if (obj != null) write(obj, yapwriter._buffer, yapwriter._offset);
         yapwriter._offset = i1 + linkLength();
      }
      
      public override int getID() {
         return typeID();
      }
      
      public override int linkLength() {
         return i_linkLength;
      }
      
      internal override Object read1(YapReader yapreader) {
         int i1 = yapreader._offset;
         Object obj1 = read(yapreader._buffer, yapreader._offset);
         yapreader._offset = i1 + linkLength();
         return obj1;
      }
      
      internal override void prepareComparison1(Object obj) {
         i_compareTo = obj;
      }
      
      internal override bool isEqual1(Object obj) {
         return isEqual(i_compareTo, obj);
      }
      
      internal override bool isGreater1(Object obj) {
         if (i_cachedClass.isInstance(obj) && !isEqual(i_compareTo, obj)) return compare(i_compareTo, obj) > 0;
         return false;
      }
      
      internal override bool isSmaller1(Object obj) {
         if (i_cachedClass.isInstance(obj) && !isEqual(i_compareTo, obj)) return compare(i_compareTo, obj) < 0;
         return false;
      }
   }
}