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
namespace com.db4o {

   internal class ByteBuffer4 {
      private int DISCARD_BUFFER_SIZE = 500;
      private byte[] i_cache;
      private bool i_closed = false;
      private int i_readOffset;
      protected int i_timeout;
      private int i_writeOffset;
      private Object i_lock = new Object();
      
      public ByteBuffer4(int i) : base() {
         i_timeout = i;
      }
      
      private int available() {
         return i_writeOffset - i_readOffset;
      }
      
      private void checkDiscardCache() {
         if (i_readOffset == i_writeOffset && i_cache.Length > 500) {
            i_cache = null;
            i_readOffset = 0;
            i_writeOffset = 0;
         }
      }
      
      internal void close() {
         i_closed = true;
      }
      
      private void makefit(int i) {
         if (i_cache == null) i_cache = new byte[i]; else if (i_writeOffset + i > i_cache.Length) {
            if (i_writeOffset + i - i_readOffset <= i_cache.Length) {
               byte[] xis1 = new byte[i_cache.Length];
               j4o.lang.JavaSystem.arraycopy(i_cache, i_readOffset, xis1, 0, i_cache.Length - i_readOffset);
               i_cache = xis1;
               i_writeOffset -= i_readOffset;
               i_readOffset = 0;
            } else {
               byte[] xis1 = new byte[i_writeOffset + i];
               j4o.lang.JavaSystem.arraycopy(i_cache, 0, xis1, 0, i_cache.Length);
               i_cache = xis1;
            }
         }
      }
      
      public int read() {
         lock (i_lock) {
            waitForAvailable();
            byte i1 = i_cache[i_readOffset++];
            checkDiscardCache();
            return i1;
         }
      }
      
      public int read(byte[] xis, int i, int i_0_) {
         lock (i_lock) {
            waitForAvailable();
            int i_1_1 = available();
            if (i_1_1 < i_0_) i_0_ = i_1_1;
            j4o.lang.JavaSystem.arraycopy(i_cache, i_readOffset, xis, i, i_0_);
            i_readOffset += i_0_;
            checkDiscardCache();
            return i_1_1;
         }
      }
      
      public void setTimeout(int i) {
         i_timeout = i;
      }
      
      private void waitForAvailable() {
         while (available() == 0) {
            try {
               {
                  j4o.lang.JavaSystem.wait(i_lock, (long)i_timeout);
               }
            }  catch (Exception exception) {
               {
                  throw new IOException(Messages.get(55));
               }
            }
         }
         if (i_closed) throw new IOException(Messages.get(35));
      }
      
      public void write(byte[] xis) {
         lock (i_lock) {
            makefit(xis.Length);
            j4o.lang.JavaSystem.arraycopy(xis, 0, i_cache, i_writeOffset, xis.Length);
            i_writeOffset += xis.Length;
            j4o.lang.JavaSystem.notify(i_lock);
         }
      }
      
      public void write(int i) {
         lock (i_lock) {
            makefit(1);
            i_cache[i_writeOffset++] = (byte)i;
            j4o.lang.JavaSystem.notify(i_lock);
         }
      }
   }
}