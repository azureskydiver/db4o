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

   internal class YapStringIO {
      
      internal YapStringIO() : base() {
      }
      protected char[] chars = new char[0];
      
      internal virtual int bytesPerChar() {
         return 1;
      }
      
      internal virtual byte encodingByte() {
         return (byte)1;
      }
      
      internal virtual int Length(String xstring) {
         return j4o.lang.JavaSystem.getLengthOf(xstring) + 0 + 4;
      }
      
      protected void checkBufferLength(int i) {
         if (i > chars.Length) chars = new char[i];
      }
      
      internal virtual String read(YapReader yapreader, int i) {
         checkBufferLength(i);
         for (int i_0_1 = 0; i_0_1 < i; i_0_1++) chars[i_0_1] = (char)(yapreader._buffer[yapreader._offset++] & 255);
         return new String(chars, 0, i);
      }
      
      internal virtual String read(byte[] xis) {
         checkBufferLength(xis.Length);
         for (int i1 = 0; i1 < xis.Length; i1++) chars[i1] = (char)(xis[i1] & 255);
         return new String(chars, 0, xis.Length);
      }
      
      internal virtual int shortLength(String xstring) {
         return j4o.lang.JavaSystem.getLengthOf(xstring) + 4;
      }
      
      protected int writetoBuffer(String xstring) {
         int i1 = j4o.lang.JavaSystem.getLengthOf(xstring);
         checkBufferLength(i1);
         j4o.lang.JavaSystem.getCharsForString(xstring, 0, i1, chars, 0);
         return i1;
      }
      
      internal virtual void write(YapReader yapreader, String xstring) {
         int i1 = writetoBuffer(xstring);
         for (int i_1_1 = 0; i_1_1 < i1; i_1_1++) yapreader._buffer[yapreader._offset++] = (byte)(chars[i_1_1] & (char)255);
      }
      
      internal virtual byte[] write(String xstring) {
         int i1 = writetoBuffer(xstring);
         byte[] xis1 = new byte[i1];
         for (int i_2_1 = 0; i_2_1 < i1; i_2_1++) xis1[i_2_1] = (byte)(chars[i_2_1] & (char)255);
         return xis1;
      }
   }
}