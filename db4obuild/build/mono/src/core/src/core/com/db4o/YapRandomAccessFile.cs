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
using com.db4o.io;
namespace com.db4o {

   internal class YapRandomAccessFile : YapFile {
      private Session i_session;
      private IoAdapter i_file;
      private IoAdapter i_timerFile;
      private volatile IoAdapter i_backupFile;
      private byte[] i_timerBytes = new byte[8];
      private Object i_fileLock;
      
      internal YapRandomAccessFile(Session session) : base((YapStream)null) {
         lock (i_lock) {
            i_fileLock = new Object();
            i_session = session;
            try {
               {
                  open();
               }
            }  catch (DatabaseFileLockedException databasefilelockedexception) {
               {
                  this.stopSession();
                  throw databasefilelockedexception;
               }
            }
            this.initialize3();
         }
      }
      
      public override void backup(String xstring) {
         lock (i_lock) {
            this.checkClosed();
            if (i_backupFile != null) Db4o.throwRuntimeException(61);
            try {
               {
                  i_backupFile = i_config.i_ioAdapter.open(xstring, true, i_file.getLength());
               }
            }  catch (Exception exception) {
               {
                  i_backupFile = null;
                  Db4o.throwRuntimeException(12, xstring);
               }
            }
         }
         long l1 = 0L;
         int i1 = 8192;
         byte[] xis1 = new byte[i1];
         do {
            lock (i_lock) {
               i_file.seek(l1);
               int i_0_1 = i_file.read(xis1);
               i_backupFile.seek(l1);
               i_backupFile.write(xis1, i_0_1);
               l1 += (long)i_0_1;
               j4o.lang.JavaSystem.notify(i_lock);
            }
         }          while (l1 < i_file.getLength());
         lock (i_lock) {
            i_backupFile.close();
            i_backupFile = null;
         }
      }
      
      internal override void blockSize(int i) {
         i_file.blockSize(i);
         if (i_timerFile != null) i_timerFile.blockSize(i);
      }
      
      internal override byte blockSize() {
         return (byte)i_file.blockSize();
      }
      
      internal override bool close2() {
         bool xbool1 = true;
         lock (Db4o.Lock) {
            xbool1 = i_session.closeInstance();
            if (xbool1) {
               this.freePrefetchedPointers();
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
               Db4o.sessionStopped(i_session);
               lock (i_fileLock) {
                  try {
                     {
                        i_file.close();
                        i_file = null;
                        if (this.needsLockFileThread()) {
                           YapWriter yapwriter1 = new YapWriter(i_systemTrans, 8);
                           YLong.writeLong(0L, yapwriter1);
                           i_timerFile.blockSeek(i_configBlock._address, 12);
                           i_timerFile.write(yapwriter1._buffer);
                           i_timerFile.close();
                        }
                     }
                  }  catch (Exception exception) {
                     {
                        i_file = null;
                        Db4o.throwRuntimeException(11, exception);
                     }
                  }
                  i_file = null;
               }
            }
         }
         return xbool1;
      }
      
      internal override void copy(int i, int i_1_, int i_2_, int i_3_, int i_4_) {
         try {
            {
               if (i_backupFile == null) i_file.blockCopy(i, i_1_, i_2_, i_3_, i_4_); else {
                  byte[] xis1 = new byte[i_4_];
                  i_file.blockSeek(i, i_1_);
                  i_file.read(xis1);
                  i_file.blockSeek(i_2_, i_3_);
                  i_file.write(xis1);
                  if (i_backupFile != null) {
                     i_backupFile.blockSeek(i_2_, i_3_);
                     i_backupFile.write(xis1);
                  }
               }
            }
         }  catch (Exception exception) {
            {
               Db4o.throwRuntimeException(16, exception);
            }
         }
      }
      
      private void checkXBytes(int i, int i_5_, int i_6_) {
      }
      
      internal override void emergencyClose() {
         base.emergencyClose();
         try {
            {
               i_file.close();
            }
         }  catch (Exception exception) {
            {
            }
         }
         try {
            {
               Db4o.sessionStopped(i_session);
            }
         }  catch (Exception exception) {
            {
            }
         }
         i_file = null;
      }
      
      internal override long fileLength() {
         try {
            {
               return i_file.getLength();
            }
         }  catch (Exception exception) {
            {
               throw new RuntimeException();
            }
         }
      }
      
      internal override String fileName() {
         return i_session.fileName();
      }
      
      private void open() {
         bool xbool1 = false;
         try {
            {
               if (j4o.lang.JavaSystem.getLengthOf(fileName()) > 0) {
                  File file1 = new File(fileName());
                  if (!file1.exists() || j4o.lang.JavaSystem.getLengthOf(file1) == 0L) {
                     xbool1 = true;
                     this.logMsg(14, fileName());
                  }
                  try {
                     {
                        bool bool_7_1 = i_config.i_lockFile && !i_config.i_readonly;
                        i_file = i_config.i_ioAdapter.open(fileName(), bool_7_1, 0L);
                        if (this.needsLockFileThread()) i_timerFile = i_config.i_ioAdapter.open(fileName(), false, 0L);
                     }
                  }  catch (DatabaseFileLockedException databasefilelockedexception) {
                     {
                        throw databasefilelockedexception;
                     }
                  } catch (Exception exception) {
                     {
                        Db4o.throwRuntimeException(12, fileName(), exception);
                     }
                  }
                  if (xbool1) {
                     if (i_config.i_reservedStorageSpace > 0) reserve(i_config.i_reservedStorageSpace);
                     this.configureNewFile();
                     this.write(false);
                     this.writeHeader(false);
                  } else this.readThis();
               } else Db4o.throwRuntimeException(21);
            }
         }  catch (Exception exception) {
            {
               if (i_references != null) i_references.stopTimer();
               throw exception;
            }
         }
      }
      
      internal override void readBytes(byte[] xis, int i, int i_8_) {
         readBytes(xis, i, 0, i_8_);
      }
      
      internal override void readBytes(byte[] xis, int i, int i_9_, int i_10_) {
         try {
            {
               i_file.blockSeek(i, i_9_);
               i_file.read(xis, i_10_);
            }
         }  catch (Exception exception) {
            {
               Db4o.throwRuntimeException(13, exception);
            }
         }
      }
      
      internal override void reserve(int i) {
         lock (i_lock) {
            int i_11_1 = this.getSlot(i);
            YapWriter yapwriter1 = new YapWriter(i_systemTrans, i_11_1, i);
            writeBytes(yapwriter1);
            this.free(i_11_1, i);
         }
      }
      
      internal override void syncFiles() {
      }
      
      internal override bool writeAccessTime() {
         if (!this.needsLockFileThread()) return true;
         lock (i_fileLock) {
            if (i_file == null) return false;
            long l1 = j4o.lang.JavaSystem.currentTimeMillis();
            YLong.writeLong(l1, i_timerBytes);
            i_timerFile.blockSeek(i_configBlock._address, 12);
            i_timerFile.write(i_timerBytes);
         }
         return true;
      }
      
      internal override void writeBytes(YapWriter yapwriter) {
         if (!i_config.i_readonly) {
            try {
               {
                  i_file.blockSeek(yapwriter.getAddress(), yapwriter.addressOffset());
                  i_file.write(yapwriter._buffer, yapwriter.getLength());
                  if (i_backupFile != null) {
                     i_backupFile.blockSeek(yapwriter.getAddress(), yapwriter.addressOffset());
                     i_backupFile.write(yapwriter._buffer, yapwriter.getLength());
                  }
               }
            }  catch (Exception exception) {
               {
                  Db4o.throwRuntimeException(16, exception);
               }
            }
         }
      }
      
      internal override void writeXBytes(int i, int i_12_) {
      }
   }
}