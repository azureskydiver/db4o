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
using com.db4o;
namespace com.db4o.io {

   public class RandomAccessFileAdapter : IoAdapter {
      private RandomAccessFile _delegate;
      private byte[] _seekBytes;
      
      public RandomAccessFileAdapter() : base() {
      }
      
      internal RandomAccessFileAdapter(String xstring, bool xbool, long l) : base() {
         _delegate = new RandomAccessFile(xstring, "rw");
         _seekBytes = null;
         if (l > 0L) {
            _delegate.seek(l - 1L);
            _delegate.write(new byte[]{
               0            });
         }
         if (xbool) Platform.Lock(_delegate);
      }
      
      public override void close() {
         try {
            {
               Platform.unlock(_delegate);
            }
         }  catch (Exception exception) {
            {
            }
         }
         _delegate.close();
      }
      
      public override long getLength() {
         return j4o.lang.JavaSystem.getLengthOf(_delegate);
      }
      
      public override IoAdapter open(String xstring, bool xbool, long l) {
         return new RandomAccessFileAdapter(xstring, xbool, l);
      }
      
      public override int read(byte[] xis, int i) {
         return _delegate.read(xis, 0, i);
      }
      
      public override void seek(long l) {
         _delegate.seek(l);
      }
      
      public override void sync() {
      }
      
      public override void write(byte[] xis, int i) {
         _delegate.write(xis, 0, i);
      }
   }
}