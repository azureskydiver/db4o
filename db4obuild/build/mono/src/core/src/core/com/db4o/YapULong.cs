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

namespace com.db4o {

    internal class YapULong : YapTypeIntegral {

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
