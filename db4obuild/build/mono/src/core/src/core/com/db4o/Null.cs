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

   internal class Null : YapComparable {
      
      internal Null() : base() {
      }
      static internal YapComparable INSTANCE = new Null();
      
      public int compareTo(Object obj) {
         if (obj == null) return 0;
         return -1;
      }
      
      public override bool Equals(Object obj) {
         return obj == null;
      }
      
      public bool isEqual(Object obj) {
         return obj == null;
      }
      
      public bool isGreater(Object obj) {
         return false;
      }
      
      public bool isSmaller(Object obj) {
         return false;
      }
      
      public YapComparable prepareComparison(Object obj) {
         return this;
      }
   }
}