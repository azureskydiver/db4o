/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace com.db4o
{
	internal class YapDouble : YapTypeIntegral
	{
        public override int compare(Object o1, Object o2){
            return ((double)o2 > (double)o1) ? 1 : -1;
        }

        public override Object defaultValue(){
            return (double)0;
        }
      
        public override Object read(byte[] bytes, int offset){
            // inverted to stay compatible with old .NET implementation
            offset += 7;
            byte[] doubleBytes = new byte[8];
            for(int i = 0; i < 8; i ++){
                doubleBytes[i] = bytes[offset--];
            }
            return BitConverter.ToDouble(doubleBytes, 0);
        }

        public override int typeID(){
            return 5;
        }
      
        public override void write(Object obj, byte[] bytes, int offset){
            // inverted to stay compatible with old .NET implementation
            offset += 7;
            byte[] doubleBytes = BitConverter.GetBytes((double)obj);
            for(int i = 0; i < 8; i ++){
                bytes[offset--] = doubleBytes[i];
            }
        }
    }
}
