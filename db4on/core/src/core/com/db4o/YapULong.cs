
/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace com.db4o {

    internal class YapULong : YapTypeIntegral {

        public YapULong(com.db4o.YapStream stream) : base(stream) {
        }

        public override int compare(Object o1, Object o2){
            return ((ulong)o2 > (ulong)o1) ? 1 : -1;
        }

        public override Object defaultValue(){
            return (ulong)0;
        }
      
        public override void write(object obj, byte[] bytes, int offset){
            ulong ul = (ulong)obj;
            for (int i = 0; i < 8; i++){
                bytes[offset++] = (byte)(int)(ul >> (7 - i) * 8);
            }
        }

        public override int typeID(){
            return 23;
        }

        public override Object read(byte[] bytes, int offset){
            ulong ul = 0;
            for (int i = 0; i < 8; i++) {
                ul = (ul << 8) + (ulong)(bytes[offset++] & 255);
            }
            return ul;
        }
    }
}
