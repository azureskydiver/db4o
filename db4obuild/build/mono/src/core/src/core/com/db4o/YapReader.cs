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

   public class YapReader {
      internal byte[] _buffer;
      internal int _offset;
      
      internal YapReader() : base() {
      }
      
      internal YapReader(int i) : base() {
         _buffer = new byte[i];
      }
      
      internal void append(byte i) {
         _buffer[_offset++] = i;
      }
      
      internal void append(byte[] xis) {
         j4o.lang.JavaSystem.arraycopy(xis, 0, _buffer, _offset, xis.Length);
         _offset += xis.Length;
      }
      
      internal bool containsTheSame(YapReader yapreader_0_) {
         if (yapreader_0_ != null) {
            byte[] xis1 = yapreader_0_._buffer;
            if (_buffer == null) return xis1 == null;
            if (xis1 != null && _buffer.Length == xis1.Length) {
               int i1 = _buffer.Length;
               for (int i_1_1 = 0; i_1_1 < i1; i_1_1++) {
                  if (_buffer[i_1_1] != xis1[i_1_1]) return false;
               }
               return true;
            }
         }
         return false;
      }
      
      internal virtual int getLength() {
         return _buffer.Length;
      }
      
      internal void incrementOffset(int i) {
         _offset += i;
      }
      
      internal void read(YapStream yapstream, int i, int i_2_) {
         yapstream.readBytes(_buffer, i, i_2_, getLength());
      }
      
      internal void readBegin(byte i) {
      }
      
      internal void readBegin(int i, byte i_3_) {
      }
      
      internal byte readByte() {
         return _buffer[_offset++];
      }
      
      internal byte[] readBytes(int i) {
         byte[] xis1 = new byte[i];
         j4o.lang.JavaSystem.arraycopy(_buffer, _offset, xis1, 0, i);
         _offset += i;
         return xis1;
      }
      
      internal YapReader readEmbeddedObject(Transaction transaction) {
         return transaction.i_stream.readObjectReaderByAddress(readInt(), readInt());
      }
      
      internal void readEncrypt(YapStream yapstream, int i) {
         yapstream.readBytes(_buffer, i, getLength());
         yapstream.i_handlers.decrypt(this);
      }
      
      internal void readEnd() {
      }
      
      internal int readInt() {
         int i1 = (_offset += 4) - 1;
         return _buffer[i1] & 255 | (_buffer[--i1] & 255) << 8 | (_buffer[--i1] & 255) << 16 | _buffer[--i1] << 24;
      }
      
      internal void replaceWith(byte[] xis) {
         j4o.lang.JavaSystem.arraycopy(xis, 0, _buffer, 0, getLength());
      }
      
      internal String ToString(Transaction transaction) {
         try {
            {
               return (String)transaction.i_stream.i_handlers.i_stringHandler.read1(this);
            }
         }  catch (Exception exception) {
            {
               return "";
            }
         }
      }
      
      internal void writeBegin(byte i) {
      }
      
      internal void writeBegin(byte i, int i_4_) {
      }
      
      internal void writeEnd() {
      }
      
      internal void writeInt(int i) {
         int i_5_1 = _offset + 4;
         _offset = i_5_1;
         byte[] xis1 = _buffer;
         xis1[--i_5_1] = (byte)i;
         xis1[--i_5_1] = (byte)(i >>= 8);
         xis1[--i_5_1] = (byte)(i >>= 8);
         xis1[--i_5_1] = (byte)(i >>= 8);
      }
   }
}