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

   internal class HashtableIntEntry : Cloneable, DeepClone {
      internal int i_key;
      internal Object i_object;
      internal HashtableIntEntry i_next;
      
      internal HashtableIntEntry(int i, Object obj) : base() {
         i_key = i;
         i_object = obj;
      }
      
      public Object deepClone(Object obj) {
         HashtableIntEntry hashtableintentry_0_1 = (HashtableIntEntry)j4o.lang.JavaSystem.clone(this);
         hashtableintentry_0_1.i_object = ((DeepClone)i_object).deepClone(obj);
         if (i_next != null) hashtableintentry_0_1.i_next = (HashtableIntEntry)i_next.deepClone(obj);
         return hashtableintentry_0_1;
      }
   }
}