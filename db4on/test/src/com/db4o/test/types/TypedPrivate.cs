/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using j4o.util;
namespace com.db4o.test.types {

   public class TypedPrivate: RTest {
      
      public TypedPrivate() : base() {
      }
      private Boolean oBoolean;
      private Boolean nBoolean;
      private bool sBoolean;
      private Byte oByte;
      private Byte nByte;
      private byte sByte;
      private Char oCharacter;
      private Char nCharacter;
      private char sChar;
      private Double oDouble;
      private Double nDouble;
      private double sDouble;
      private Single oFloat;
      private Single nFloat;
      private float sFloat;
      private Int32 oInteger;
      private Int32 nInteger;
      private int sInteger;
      private Int64 oLong;
      private Int64 nLong;
      private long sLong;
      private Int16 oShort;
      private Int16 nShort;
      private short sShort;
      private String oString;
      private String nString;
      private ObjectSimplePrivate oObject;
      private ObjectSimplePrivate nObject;
      
      public override void set(int ver) {
         if (ver == 1) {
            oBoolean = System.Convert.ToBoolean(true);
            nBoolean = true;
            sBoolean = false;
            oByte = System.Convert.ToByte(Byte.MaxValue);
            nByte = (Byte.MinValue + 1);
            sByte = Byte.MinValue;
            oCharacter = System.Convert.ToChar((char)(Char.MaxValue - 1));
            nCharacter = char.MinValue;
            sChar = Char.MinValue;
            oDouble = System.Convert.ToDouble(Double.MaxValue - 1);
            nDouble = 0;
            sDouble = Double.MinValue;
            oFloat = System.Convert.ToSingle(Single.MaxValue - 1);
            nFloat = float.MinValue;
            sFloat = Single.MinValue;
            oInteger = System.Convert.ToInt32(Int32.MaxValue - 1);
            nInteger = 5;
            sInteger = Int32.MinValue;
            oLong = System.Convert.ToInt64(Int64.MaxValue - 1);
            nLong = Int64.MinValue;
            sLong = Int64.MinValue;
            oShort = System.Convert.ToInt16((short)(Int16.MaxValue - 1));
            nShort = Int16.MinValue;
            sShort = Int16.MinValue;
            oString = "db4o rules";
            nString = null;
            oObject = new ObjectSimplePrivate("s1");
            nObject = null;
         } else {
            oBoolean = System.Convert.ToBoolean(false);
            nBoolean = System.Convert.ToBoolean(true);
            sBoolean = true;
            oByte = System.Convert.ToByte((byte)0);
            nByte = System.Convert.ToByte(Byte.MinValue);
            sByte = Byte.MaxValue;
            oCharacter = System.Convert.ToChar((char)0);
            nCharacter = System.Convert.ToChar(Char.MinValue);
            sChar = (char)(Char.MaxValue - 1);
            oDouble = System.Convert.ToDouble(0);
            nDouble = System.Convert.ToDouble(Double.MinValue);
            sDouble = Double.MaxValue - 1;
            oFloat = System.Convert.ToSingle(0);
            nFloat = System.Convert.ToSingle(Single.MinValue);
            sFloat = Single.MaxValue - 1;
            oInteger = System.Convert.ToInt32(0);
            nInteger = System.Convert.ToInt32(Int32.MinValue);
            sInteger = Int32.MaxValue - 1;
            oLong = System.Convert.ToInt64(0);
            nLong = System.Convert.ToInt64(Int64.MinValue);
            sLong = Int64.MaxValue - 1;
            oShort = System.Convert.ToInt16((short)0);
            nShort = System.Convert.ToInt16(Int16.MinValue);
            sShort = (short)(Int16.MaxValue - 1);
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