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
namespace com.db4o.test.types {

   public class IntEntry: Entry {
      
      public IntEntry() : base() {
      }
      
      public override Entry firstElement() {
         return new Entry(System.Convert.ToInt32(101), "firstvalue");
      }
      
      public override Entry lastElement() {
         return new Entry(System.Convert.ToInt32(9999999), new ObjectSimplePublic("lastValue"));
      }
      
      public override Entry noElement() {
         return new Entry(System.Convert.ToInt32(-99999), "babe");
      }
      
      public override Entry[] test(int ver) {
         if (ver == 1) {
            return new Entry[]{firstElement(), new Entry(System.Convert.ToInt32(111), new ObjectSimplePublic("111")), new Entry(System.Convert.ToInt32(9999111), System.Convert.ToDouble((double)0.4566)), lastElement()};
         }
         return new Entry[]{new Entry(System.Convert.ToInt32(222), new ObjectSimplePublic("111")), new Entry(System.Convert.ToInt32(333), "TrippleThree"), new Entry(System.Convert.ToInt32(4444), new ObjectSimplePublic("4444"))};
      }
   }
}