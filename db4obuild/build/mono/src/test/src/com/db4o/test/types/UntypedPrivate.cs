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

   public class UntypedPrivate: RTest {
      
      public UntypedPrivate() : base() {
      }
      private Object oBoolean;
      private Object nBoolean;
      private Object oByte;
      private Object nByte;
      private Object oCharacter;
      private Object nCharacter;
      private Object oDouble;
      private Object nDouble;
      private Object oFloat;
      private Object nFloat;
      private Object oInteger;
      private Object nInteger;
      private Object oLong;
      private Object nLong;
      private Object oShort;
      private Object nShort;
      private Object oString;
      private Object nString;
      private Object oObject;
      private Object nObject;
      
      public override void set(int ver) {
         if (ver == 1) {
            oBoolean = System.Convert.ToBoolean(true);
            nBoolean = null;
            oByte = System.Convert.ToByte(Byte.MaxValue);
            nByte = null;
            oCharacter = System.Convert.ToChar((char)(Char.MaxValue - 1));
            nCharacter = null;
            oDouble = System.Convert.ToDouble(Double.MaxValue - 1);
            nDouble = null;
            oFloat = System.Convert.ToSingle(Single.MaxValue - 1);
            nFloat = null;
            oInteger = System.Convert.ToInt32(Int32.MaxValue - 1);
            nInteger = null;
            oLong = System.Convert.ToInt64(Int64.MaxValue - 1);
            nLong = null;
            oShort = System.Convert.ToInt16((short)(Int16.MaxValue - 1));
            nShort = null;
            oString = "db4o rules";
            nString = null;
            oObject = new ObjectSimplePrivate("s1");
            nObject = null;
         } else {
            oBoolean = System.Convert.ToBoolean(false);
            nBoolean = System.Convert.ToBoolean(true);
            oByte = System.Convert.ToByte((byte)0);
            nByte = System.Convert.ToByte(Byte.MinValue);
            oCharacter = System.Convert.ToChar((char)0);
            nCharacter = System.Convert.ToChar(Char.MinValue);
            oDouble = System.Convert.ToDouble(0);
            nDouble = System.Convert.ToDouble(Double.MinValue);
            oFloat = System.Convert.ToSingle(0);
            nFloat = System.Convert.ToSingle(Single.MinValue);
            oInteger = System.Convert.ToInt32(0);
            nInteger = System.Convert.ToInt32(Int32.MinValue);
            oLong = System.Convert.ToInt64(0);
            nLong = System.Convert.ToInt64(Int64.MinValue);
            oShort = System.Convert.ToInt16((short)0);
            nShort = System.Convert.ToInt16(Int16.MinValue);
            oString = "db4o rules of course";
            nString = "yeah";
            oObject = new ObjectSimplePrivate("s2o");
            nObject = new ObjectSimplePrivate("s2n");
         }
      }
      
      public override bool jdk2() {
         return true;
      }
   }
}