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
namespace com.db4o.ext {

   public class MemoryFile {
      private byte[] i_bytes;
      private int INITIAL_SIZE_AND_INC = 10000;
      private int i_initialSize = 10000;
      private int i_incrementSizeBy = 10000;
      
      public MemoryFile() : base() {
      }
      
      public MemoryFile(byte[] xis) : base() {
         i_bytes = xis;
      }
      
      public byte[] getBytes() {
         if (i_bytes == null) return new byte[0];
         return i_bytes;
      }
      
      public int getIncrementSizeBy() {
         return i_incrementSizeBy;
      }
      
      public int getInitialSize() {
         return i_initialSize;
      }
      
      public void setBytes(byte[] xis) {
         i_bytes = xis;
      }
      
      public void setIncrementSizeBy(int i) {
         i_incrementSizeBy = i;
      }
      
      public void setInitialSize(int i) {
         i_initialSize = i;
      }
   }
}