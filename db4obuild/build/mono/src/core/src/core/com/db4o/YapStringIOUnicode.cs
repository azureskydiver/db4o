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
namespace com.db4o {

   internal class YapStringIOUnicode : YapStringIO {
      
      internal YapStringIOUnicode() : base() {
      }
      
      internal override int bytesPerChar() {
         return 2;
      }
      
      internal override byte encodingByte() {
         return (byte)2;
      }
      
      internal override int Length(String xstring) {
         return j4o.lang.JavaSystem.getLengthOf(xstring) * 2 + 0 + 4;
      }
      
      internal override String read(YapReader yapreader, int i) {
         this.checkBufferLength(i);
         for (int i_0_1 = 0; i_0_1 < i; i_0_1++) chars[i_0_1] = (char)(yapreader._buffer[yapreader._offset++] & 255 | (yapreader._buffer[yapreader._offset++] & 255) << 8);
         return new String(chars, 0, i);
      }
      
      internal override String read(byte[] xis) {
         int i1 = xis.Length / 2;
         this.checkBufferLength(i1);
         int i_1_1 = 0;
         for (int i_2_1 = 0; i_2_1 < i1; i_2_1++) chars[i_2_1] = (char)(xis[i_1_1++] & 255 | (xis[i_1_1++] & 255) << 8);
         return new String(chars, 0, i1);
      }
      
      internal override int shortLength(String xstring) {
         return j4o.lang.JavaSystem.getLengthOf(xstring) * 2 + 4;
      }
      
      internal override void write(YapReader yapreader, String xstring) {
         int i1 = this.writetoBuffer(xstring);
         for (int i_3_1 = 0; i_3_1 < i1; i_3_1++) {
            yapreader._buffer[yapreader._offset++] = (byte)(chars[i_3_1] & (char)255);
            yapreader._buffer[yapreader._offset++] = (byte)(chars[i_3_1] >> 8);
         }
      }
      
      internal override byte[] write(String xstring) {
         int i1 = this.writetoBuffer(xstring);
         byte[] xis1 = new byte[i1 * 2];
         int i_4_1 = 0;
         for (int i_5_1 = 0; i_5_1 < i1; i_5_1++) {
            xis1[i_4_1++] = (byte)(chars[i_5_1] & (char)255);
            xis1[i_4_1++] = (byte)(chars[i_5_1] >> 8);
         }
         return xis1;
      }
   }
}