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
using j4o.io;
namespace com.db4o.io {

   public abstract class IoAdapter {
      
      public IoAdapter() : base() {
      }
      private int _blockSize;
      
      protected long regularAddress(int i, int i_0_) {
         return (long)i * (long)_blockSize + (long)i_0_;
      }
      
      public void blockCopy(int i, int i_1_, int i_2_, int i_3_, int i_4_) {
         copy(regularAddress(i, i_1_), regularAddress(i_2_, i_3_), i_4_);
      }
      
      public void blockSeek(int i) {
         blockSeek(i, 0);
      }
      
      public void blockSeek(int i, int i_5_) {
         seek(regularAddress(i, i_5_));
      }
      
      public void blockSize(int i) {
         _blockSize = i;
      }
      
      public abstract void close();
      
      public void copy(long l, long l_6_, int i) {
         byte[] xis1 = new byte[i];
         seek(l);
         read(xis1);
         seek(l_6_);
         write(xis1);
      }
      
      public abstract long getLength();
      
      public abstract IoAdapter open(String xstring, bool xbool, long l);
      
      public int read(byte[] xis) {
         return read(xis, xis.Length);
      }
      
      public abstract int read(byte[] xis, int i);
      
      public abstract void seek(long l);
      
      public abstract void sync();
      
      public void write(byte[] xis) {
         write(xis, xis.Length);
      }
      
      public abstract void write(byte[] xis, int i);
      
      public int blockSize() {
         return _blockSize;
      }
   }
}