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
