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

namespace com.db4o
{
	internal class YapUShort : YapTypeIntegral
	{
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
