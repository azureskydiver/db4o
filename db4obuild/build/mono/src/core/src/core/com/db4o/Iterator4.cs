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

   public class Iterator4 {
      private List4 i_next;
      
      internal Iterator4(List4 list4) : base() {
         i_next = list4;
      }
      
      public virtual bool hasNext() {
         return i_next != null;
      }
      
      public Object next() {
         Object obj1 = i_next.i_object;
         i_next = i_next.i_next;
         return obj1;
      }
   }
}