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

   internal class Queue4 {
      
      internal Queue4() : base() {
      }
      private List4 i_first;
      private List4 i_last;
      
      internal void add(Object obj) {
         List4 list41 = new List4(null, obj);
         if (i_first == null) i_last = list41; else i_first.i_next = list41;
         i_first = list41;
      }
      
      internal Object next() {
         if (i_last == null) return null;
         Object obj1 = i_last.i_object;
         i_last = i_last.i_next;
         if (i_last == null) i_first = null;
         return obj1;
      }
   }
}