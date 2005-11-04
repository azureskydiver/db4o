/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace com.db4o
{
	internal class YapUInt : YapTypeIntegral
	{

        public YapUInt(com.db4o.YapStream stream) : base(stream) {
        }

        public override int compare(Object o1, Object o2){
            return ((uint)o2 > (uint)o1) ? 1 : -1;
        }

        public override Object defaultValue(){
            return (uint)0;
        }
      
        public override Object read(byte[] bytes, int offset){
            offset += 3;
            return (uint) (bytes[offset] & 255 | (bytes[--offset] & 255) << 8 | (bytes[--offset] & 255) << 16 | bytes[--offset] << 24);
        }

        public override int typeID(){
            return 22;
        }
      
        public override void write(Object obj, byte[] bytes, int offset){
            uint ui = (uint)obj;
            offset += 4;
            bytes[--offset] = (byte)ui;
            bytes[--offset] = (byte)(ui >>= 8);
            bytes[--offset] = (byte)(ui >>= 8);
            bytes[--offset] = (byte)(ui >>= 8);
        }
    }
}
