/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using j4o.util;
namespace com.db4o.test.types {

   public class UntypedPublic: RTest {
      
      public UntypedPublic() : base() {
      }
      public Object oBoolean;
      public Object nBoolean;
      public Object oByte;
      public Object nByte;
      public Object oCharacter;
      public Object nCharacter;
      public Object oDouble;
      public Object nDouble;
      public Object oFloat;
      public Object nFloat;
      public Object oInteger;
      public Object nInteger;
      public Object oLong;
      public Object nLong;
      public Object oShort;
      public Object nShort;
      public Object oString;
      public Object nString;
      public Object oObject;
      public Object nObject;
      
      public override void Set(int ver) {
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
            oObject = new ObjectSimplePublic("s1");
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
            oObject = new ObjectSimplePublic("s2o");
            nObject = new ObjectSimplePublic("s2n");
         }
      }
   }
}