﻿/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace com.db4o
{
	internal class YapSByte : YapTypeIntegral
	{
        public YapSByte(com.db4o.YapStream stream) : base(stream) {
        }

        public override int Compare(Object o1, Object o2){
            return ((sbyte)o2 > (sbyte)o1) ? 1 : -1;
        }

        public override Object DefaultValue(){
            return (sbyte)0;
        }
      
        public override Object Read(byte[] bytes, int offset){
            return (sbyte)  ((bytes[offset]) - 128) ;
        }

        public override int TypeID(){
            return 20;
        }
      
        public override void Write(Object obj, byte[] bytes, int offset){
            bytes[offset] = (byte)(((sbyte)obj) + 128);
        }
      
    }
}
