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
using j4o.util;
namespace com.db4o {

   internal class YDate : YLong {
      
      internal YDate() : base() {
      }
      private static Class i_class = j4o.lang.Class.getClassForObject(new Date(0L));
      
      public override void copyValue(Object obj, Object obj_0_) {
         try {
            {
               ((Date)obj_0_).setTime(((Date)obj).getTime());
            }
         }  catch (Exception exception) {
            {
            }
         }
      }
      
      public override int getID() {
         return 10;
      }
      
      public override Class getJavaClass() {
         return i_class;
      }
      
      public override Class getPrimitiveJavaClass() {
         return null;
      }
      
      internal override Object primitiveNull() {
         return null;
      }
      
      internal override Object read1(YapReader yapreader) {
         long l1 = YLong.readLong(yapreader);
         if (l1 == 9223372036854775807L) return null;
         return new Date(l1);
      }
      
      public override void write(Object obj, YapWriter yapwriter) {
         if (obj == null) YLong.writeLong(9223372036854775807L, yapwriter); else YLong.writeLong(((Date)obj).getTime(), yapwriter);
      }
      
      static internal String now() {
         return Platform.format(new Date(), true);
      }
      
      internal override long val(Object obj) {
         return ((Date)obj).getTime();
      }
      
      internal override bool isEqual1(Object obj) {
         return obj is Date && val(obj) == i_compareTo;
      }
      
      internal override bool isGreater1(Object obj) {
         return obj is Date && val(obj) > i_compareTo;
      }
      
      internal override bool isSmaller1(Object obj) {
         return obj is Date && val(obj) < i_compareTo;
      }
   }
}