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

   internal class YapBit {
      private int i_value;
      
      internal YapBit(int i) : base() {
         i_value = i;
      }
      
      internal void set(bool xbool) {
         i_value = i_value * 2;
         if (xbool) i_value++;
      }
      
      internal bool get() {
         double d1 = (double)i_value / 2.0;
         i_value = i_value / 2;
         return d1 != (double)i_value;
      }
      
      internal byte getByte() {
         return (byte)i_value;
      }
   }
}