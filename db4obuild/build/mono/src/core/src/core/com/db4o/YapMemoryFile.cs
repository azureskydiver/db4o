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
using com.db4o.ext;
namespace com.db4o {

   internal class YapMemoryFile : YapFile {
      private bool i_closed = false;
      internal MemoryFile i_memoryFile;
      private int i_length = 0;
      
      internal YapMemoryFile(YapStream yapstream, MemoryFile memoryfile) : base(yapstream) {
         i_memoryFile = memoryfile;
         try {
            {
               open();
            }
         }  catch (Exception exception) {
            {
               Db4o.throwRuntimeException(22, exception);
            }
         }
         this.initialize3();
      }
      
      internal YapMemoryFile(MemoryFile memoryfile) : this(null, memoryfile) {
      }
      
      public override void backup(String xstring) {
         Db4o.throwRuntimeException(60);
      }
      
      internal void checkDemoHop() {
      }
      
      internal override bool close2() {
         try {
            {
               i_entryCounter++;
               this.write(true);
            }
         }  catch (Exception throwable) {
            {
               this.fatalException(throwable);
            }
         }
         base.close2();
         i_entryCounter--;
         if (!i_closed) {
            byte[] xis1 = new byte[i_length];
            j4o.lang.JavaSystem.arraycopy(i_memoryFile.getBytes(), 0, xis1, 0, i_length);
            i_memoryFile.setBytes(xis1);
         }
         i_closed = true;
         return true;
      }
      
      internal override void copy(int i, int i_0_, int i_1_, int i_2_, int i_3_) {
         byte[] xis1 = memoryFileBytes(i_1_ + i_2_ + i_3_);
         j4o.lang.JavaSystem.arraycopy(xis1, i + i_0_, xis1, i_1_ + i_2_, i_3_);
      }
      
      internal override void emergencyClose() {
         base.emergencyClose();
         i_closed = true;
      }
      
      internal override long fileLength() {
         return (long)i_length;
      }
      
      internal override String fileName() {
         return "Memory File";
      }
      
      internal override bool hasShutDownHook() {
         return false;
      }
      
      internal override bool needsLockFileThread() {
         return false;
      }
      
      private void open() {
         byte[] xis1 = i_memoryFile.getBytes();
         if (xis1 == null || xis1.Length == 0) {
            i_memoryFile.setBytes(new byte[i_memoryFile.getInitialSize()]);
            this.configureNewFile();
            this.write(false);
            this.writeHeader(false);
         } else {
            i_length = xis1.Length;
            this.readThis();
         }
      }
      
      internal override void readBytes(byte[] xis, int i, int i_4_) {
         try {
            {
               j4o.lang.JavaSystem.arraycopy(i_memoryFile.getBytes(), i, xis, 0, i_4_);
            }
         }  catch (Exception exception) {
            {
               Db4o.throwRuntimeException(13, exception);
            }
         }
      }
      
      internal override void readBytes(byte[] xis, int i, int i_5_, int i_6_) {
         readBytes(xis, i + i_5_, i_6_);
      }
      
      internal override void syncFiles() {
      }
      
      internal override bool writeAccessTime() {
         return true;
      }
      
      internal override void writeBytes(YapWriter yapwriter) {
         int i1 = yapwriter.getAddress() + yapwriter.addressOffset();
         int i_7_1 = yapwriter.getLength();
         j4o.lang.JavaSystem.arraycopy(yapwriter._buffer, 0, memoryFileBytes(i1 + i_7_1), i1, i_7_1);
      }
      
      private byte[] memoryFileBytes(int i) {
         byte[] xis1 = i_memoryFile.getBytes();
         if (i > i_length) {
            if (i > xis1.Length) {
               int i_8_1 = i - xis1.Length;
               if (i_8_1 < i_memoryFile.getIncrementSizeBy()) i_8_1 = i_memoryFile.getIncrementSizeBy();
               byte[] is_9_1 = new byte[xis1.Length + i_8_1];
               j4o.lang.JavaSystem.arraycopy(xis1, 0, is_9_1, 0, xis1.Length);
               i_memoryFile.setBytes(is_9_1);
               xis1 = is_9_1;
            }
            i_length = i;
         }
         return xis1;
      }
      
      internal override void writeXBytes(int i, int i_10_) {
      }
   }
}