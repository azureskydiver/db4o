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

   internal class Order : Orderable {
      
      internal Order() : base() {
      }
      private int i_major;
      private int i_minor;
      
      public int compareTo(Object obj) {
         if (obj is Order) {
            Order order_0_1 = (Order)obj;
            int i1 = i_major - order_0_1.i_major;
            if (i1 != 0) return i1;
            return i_minor - order_0_1.i_minor;
         }
         return 1;
      }
      
      public void hintOrder(int i, bool xbool) {
         if (xbool) i_major = i; else i_minor = i;
      }
      
      public bool hasDuplicates() {
         return true;
      }
   }
}