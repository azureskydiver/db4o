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

    public class ArrayTypedPublic: RTest {
      
        public ArrayTypedPublic() : base() {
        }
        public Boolean[] oBoolean;
        public Boolean[] nBoolean;
        public bool[] sBoolean;
        public Byte[] oByte;
        public Byte[] nByte;
        public byte[] sByte;
        public Char[] oCharacter;
        public Char[] nCharacter;
        public char[] sChar;
        public Double[] oDouble;
        public Double[] nDouble;
        public double[] sDouble;
        public Single[] oFloat;
        public Single[] nFloat;
        public float[] sFloat;
        public Int32[] oInteger;
        public Int32[] nInteger;
        public int[] sInteger;
        public Int64[] oLong;
        public Int64[] nLong;
        public long[] sLong;
        public Int16[] oShort;
        public Int16[] nShort;
        public short[] sShort;
        public String[] oString;
        public String[] nString;
        public ObjectSimplePublic[] oObject;
        public ObjectSimplePublic[] nObject;
      
        public override void set(int ver) {
            if (ver == 1) {
                oBoolean = new Boolean[]{System.Convert.ToBoolean(true), System.Convert.ToBoolean(false)};
                nBoolean = null;
                sBoolean = new bool[]{true, true, false};
                oByte = new Byte[]{System.Convert.ToByte(Byte.MaxValue), System.Convert.ToByte(Byte.MinValue), System.Convert.ToByte((byte)0)};
                nByte = null;
                sByte = new byte[]{Byte.MaxValue, Byte.MinValue, 0, 1};
                oCharacter = new Char[]{System.Convert.ToChar((char)(Char.MaxValue - 1)), System.Convert.ToChar((char)Char.MinValue), System.Convert.ToChar((char)0)};
                nCharacter = null;
                sChar = new char[]{(char)(Char.MaxValue - 1), (char)Char.MinValue, (char)0};
                oDouble = new Double[]{Double.MaxValue - 1, Double.MinValue, (double)0};
                nDouble = null;
                sDouble = new double[]{Double.MaxValue - 1, Double.MinValue, 0};
                oFloat = new Single[]{System.Convert.ToSingle(Single.MaxValue - 1), System.Convert.ToSingle(Single.MinValue), System.Convert.ToSingle(0)};
                nFloat = null;
                sFloat = new float[]{Single.MaxValue - 1, Single.MinValue, 0};
                oInteger = new Int32[]{System.Convert.ToInt32(Int32.MaxValue - 1), System.Convert.ToInt32(Int32.MinValue), System.Convert.ToInt32(0)};
                nInteger = null;
                sInteger = new int[]{Int32.MaxValue - 1, Int32.MinValue, 0};
                oLong = new Int64[]{System.Convert.ToInt64(Int64.MaxValue - 1), System.Convert.ToInt64(Int64.MinValue), System.Convert.ToInt64(0)};
                nLong = null;
                sLong = new long[]{Int64.MaxValue - 1, Int64.MinValue, 0};
                oShort = new Int16[]{System.Convert.ToInt16((short)(Int16.MaxValue - 1)), System.Convert.ToInt16((short)Int16.MinValue), System.Convert.ToInt16((short)0)};
                nShort = null;
                sShort = new short[]{(short)(Int16.MaxValue - 1), (short)Int16.MinValue, (short)0};
                oString = new String[]{"db4o rules", "cool", "supergreat"};
                nString = null;
                oObject = new ObjectSimplePublic[]{new ObjectSimplePublic("so"), null, new ObjectSimplePublic("far"), new ObjectSimplePublic("O.K.")};
                nObject = null;
            } else {
                oBoolean = new Boolean[]{System.Convert.ToBoolean(false), System.Convert.ToBoolean(true), System.Convert.ToBoolean(true)};
                nBoolean = new Boolean[]{System.Convert.ToBoolean(true), System.Convert.ToBoolean(false)};
                sBoolean = new bool[]{true, true, true};
                oByte = new Byte[]{System.Convert.ToByte(Byte.MinValue), System.Convert.ToByte(Byte.MaxValue), System.Convert.ToByte((byte)1), System.Convert.ToByte((byte)0)};
                nByte = new Byte[]{System.Convert.ToByte(Byte.MaxValue), System.Convert.ToByte(Byte.MinValue), System.Convert.ToByte((byte)0)};
                sByte = new byte[]{Byte.MinValue, Byte.MaxValue, 0, 1};
                oCharacter = new Char[]{System.Convert.ToChar((char)Char.MinValue), System.Convert.ToChar((char)(Char.MaxValue - 1)), System.Convert.ToChar((char)0), System.Convert.ToChar((char)(Char.MaxValue - 1)), System.Convert.ToChar((char)1)};
                nCharacter = new Char[]{System.Convert.ToChar((char)(Char.MaxValue - 1)), System.Convert.ToChar((char)Char.MinValue), System.Convert.ToChar((char)0)};
                sChar = new char[]{(char)Char.MinValue, (char)0};
                oDouble = new Double[]{Double.MinValue, (double) 0};
                nDouble = new Double[]{Double.MaxValue - 1, Double.MinValue, (double)-123.12344, (double)-12345.123445566};
                sDouble = new double[]{Double.MaxValue - 1, Double.MinValue, 0, (double)0.12344, (double)-123.12344};
                oFloat = new Single[]{System.Convert.ToSingle((float)-98.765)};
                nFloat = null;
                sFloat = new float[]{(float)-0.55, Single.MaxValue - 1, Single.MinValue, 0, (float)0.33};
                oInteger = new Int32[]{System.Convert.ToInt32(Int32.MaxValue - 1), System.Convert.ToInt32(Int32.MinValue), System.Convert.ToInt32(111), System.Convert.ToInt32(-333)};
                nInteger = new Int32[]{System.Convert.ToInt32(Int32.MaxValue - 1), System.Convert.ToInt32(Int32.MinValue), System.Convert.ToInt32(0)};
                sInteger = new int[]{888, 666, 999, 101010, 111111};
                oLong = new Int64[]{System.Convert.ToInt64(Int64.MaxValue - 1), System.Convert.ToInt64(Int64.MinValue), System.Convert.ToInt64(1)};
                nLong = new Int64[]{System.Convert.ToInt64(Int64.MaxValue - 1), System.Convert.ToInt64(Int64.MinValue), System.Convert.ToInt64(0)};
                sLong = new long[]{Int64.MaxValue - 1, Int64.MinValue};
                oShort = new Int16[]{System.Convert.ToInt16((short)Int16.MinValue), System.Convert.ToInt16((short)(Int16.MaxValue - 1)), System.Convert.ToInt16((short)0)};
                nShort = new Int16[]{System.Convert.ToInt16((short)(Int16.MaxValue - 1)), System.Convert.ToInt16((short)Int16.MinValue), System.Convert.ToInt16((short)0)};
                sShort = null;
                oString = new String[]{"db4o rulez", "cool", "supergreat"};
                nString = new String[]{null, "db4o rules", "cool", "supergreat", null};
                oObject = new ObjectSimplePublic[]{new ObjectSimplePublic("works"), new ObjectSimplePublic("far"), new ObjectSimplePublic("excellent")};
                nObject = new ObjectSimplePublic[]{};
            }
        }
    }
}