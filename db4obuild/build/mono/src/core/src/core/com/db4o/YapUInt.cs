/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
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
