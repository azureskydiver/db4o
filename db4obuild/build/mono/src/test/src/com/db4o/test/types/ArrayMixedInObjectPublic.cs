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
namespace com.db4o.test.types {

   public class ArrayMixedInObjectPublic: RTest {
      
      public ArrayMixedInObjectPublic() : base() {
      }
      public Object o1;
      public Object o2;
      public Object o3;
      public Object o4;
      public Object o5;
      
      public override void set(int ver) {
         if (ver == 1) {
            o1 = new Boolean[]{System.Convert.ToBoolean(true), System.Convert.ToBoolean(false)};
            o2 = null;
            o3 = new Byte[]{System.Convert.ToByte(Byte.MaxValue), System.Convert.ToByte(Byte.MinValue), System.Convert.ToByte((byte)0)};
            o4 = new Single[]{System.Convert.ToSingle(Single.MaxValue - 1), System.Convert.ToSingle(Single.MinValue), System.Convert.ToSingle(0)};
            o5 = new String[]{"db4o rules", "cool", "supergreat"};
         } else {
            o1 = new Object[]{"ohje", System.Convert.ToDouble(Double.MinValue), System.Convert.ToSingle(4)};
            o2 = null;
            o3 = new String[]{};
            o4 = new Boolean[]{System.Convert.ToBoolean(false), System.Convert.ToBoolean(true), System.Convert.ToBoolean(true)};
            o5 = new Double[]{System.Convert.ToDouble(Double.MinValue), System.Convert.ToDouble(0)};
         }
      }
   }
}