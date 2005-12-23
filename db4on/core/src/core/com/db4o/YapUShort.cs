/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace com.db4o
{
	internal class YapUShort : YapTypeIntegral
	{

        public YapUShort(com.db4o.YapStream stream) : base(stream) {
        }

        public override int compare(Object o1, Object o2){
            return ((ushort)o2 > (ushort)o1) ? 1 : -1;
        }

        public override Object defaultValue(){
            return (ushort)0;
        }
      
        public override Object read(byte[] bytes, int offset){
            offset += 1;
            return (ushort) (bytes[offset] & 255 | (bytes[--offset] & 255) << 8);
        }
      
        public override int typeID(){
            return 24;
        }
      
        public override void write(Object obj, byte[] bytes, int offset){
            ushort us = (ushort)obj;
            offset += 2;
            bytes[--offset] = (byte)us;
            bytes[--offset] = (byte)(us >>= 8);
        }
    }

}
