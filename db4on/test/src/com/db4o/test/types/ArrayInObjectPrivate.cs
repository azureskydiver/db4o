/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using j4o.util;
namespace com.db4o.test.types {

   public class ArrayInObjectPrivate: RTest {
      
      public ArrayInObjectPrivate() : base() {
      }
      private Object oBoolean;
      private Object nBoolean;
      private Object oByte;
      private Object nByte;
      private Object oCharacter;
      private Object nCharacter;
      private Object oDouble;
      private Object nDouble;
      // private Object oFloat;   weird problem here
      private Object nFloat;
      private Object oInteger;
      private Object nInteger;
      private Object oLong;
      private Object nLong;
      private Object oString;
      private Object nString;
      
      public override void set(int ver) {
         if (ver == 1) {
            oBoolean = new bool[]{true, false};
            nBoolean = null;
            oByte = new byte[]{byte.MaxValue , byte.MinValue, 0};
            nByte = null;
            oCharacter = new char[]{(char)(char.MaxValue - 1), char.MinValue, (char)0};
            nCharacter = null;
            oDouble = new double[] {double.MaxValue - 1, double.MinValue, 0};
            nDouble = null;
            // oFloat = new Single[]{System.Convert.ToSingle(Single.MaxValue - 1), System.Convert.ToSingle(Single.MinValue), System.Convert.ToSingle(0)};
            nFloat = null;
            oInteger = new Int32[]{System.Convert.ToInt32(Int32.MaxValue - 1), System.Convert.ToInt32(Int32.MinValue), System.Convert.ToInt32(0)};
            nInteger = null;
            oLong = new Int64[]{Int64.MaxValue - 1, System.Convert.ToInt64(Int64.MinValue), System.Convert.ToInt64(0)};
            nLong = null;
            oString = new String[]{"db4o rules", "cool", "supergreat"};
            nString = null;
         } else {
            oBoolean = new Boolean[]{System.Convert.ToBoolean(false), System.Convert.ToBoolean(true), System.Convert.ToBoolean(true)};
            nBoolean = new Boolean[]{System.Convert.ToBoolean(true), System.Convert.ToBoolean(false)};
            oByte = new byte[]{System.Convert.ToByte(Byte.MinValue), System.Convert.ToByte(Byte.MaxValue), System.Convert.ToByte((byte)1), System.Convert.ToByte((byte)0)};
            nByte = new byte[]{System.Convert.ToByte(Byte.MaxValue), System.Convert.ToByte(Byte.MinValue), System.Convert.ToByte((byte)0)};
            oCharacter = new char[]{System.Convert.ToChar((char)Char.MinValue), System.Convert.ToChar((char)(Char.MaxValue - 1)), System.Convert.ToChar((char)0), System.Convert.ToChar((char)(Char.MaxValue - 1)), System.Convert.ToChar((char)1)};
            nCharacter = new char[]{System.Convert.ToChar((char)(Char.MaxValue - 1)), System.Convert.ToChar((char)Char.MinValue), System.Convert.ToChar((char)0)};
            oDouble = new double[]{System.Convert.ToDouble(Double.MinValue), System.Convert.ToDouble(0)};
            nDouble = new double[]{System.Convert.ToDouble(Double.MaxValue - 1), System.Convert.ToDouble(Double.MinValue), System.Convert.ToDouble((double)-123.12344), System.Convert.ToDouble((double)-12345.123445566)};
            oInteger = new Int32[]{System.Convert.ToInt32(Int32.MaxValue - 1), System.Convert.ToInt32(Int32.MinValue), System.Convert.ToInt32(111), System.Convert.ToInt32(-333)};
            nInteger = new Int32[]{System.Convert.ToInt32(Int32.MaxValue - 1), System.Convert.ToInt32(Int32.MinValue), System.Convert.ToInt32(0)};
            oLong = new Int64[]{System.Convert.ToInt64(Int64.MaxValue - 1), System.Convert.ToInt64(Int64.MinValue), System.Convert.ToInt64(1)};
            nLong = new Int64[]{System.Convert.ToInt64(Int64.MaxValue - 1), System.Convert.ToInt64(Int64.MinValue), System.Convert.ToInt64(0)};
            oString = new String[]{"db4o rulez", "cool", "supergreat"};
            nString = new String[]{null, "db4o rules", "cool", "supergreat", null};
         }
      }
      
      public override bool jdk2() {
         return false;
      }
   }
}