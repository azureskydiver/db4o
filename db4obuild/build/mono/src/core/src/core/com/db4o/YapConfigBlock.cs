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
using com.db4o.foundation;
namespace com.db4o {

   internal class YapConfigBlock : Runnable {
      private Object _timeWriterLock = new Object();
      private YapFile _stream;
      internal int _address;
      private Transaction _transactionToCommit;
      internal int _bootRecordID;
      private static int POINTER_ADDRESS = 2;
      private static int MINIMUM_LENGTH = 21;
      static internal int OPEN_TIME_OFFSET = 4;
      static internal int ACCESS_TIME_OFFSET = 12;
      static internal int TRANSACTION_OFFSET = 21;
      private static int BOOTRECORD_OFFSET = 29;
      private static int BLOCKLENGTH_OFFSET = 33;
      private static int LENGTH = 37;
      private long _opentime;
      internal byte _encoding;
      
      internal YapConfigBlock(YapFile yapfile) : base() {
         _stream = yapfile;
         _opentime = processID();
         if (lockFile()) writeHeaderLock();
      }
      
      internal YapConfigBlock(YapFile yapfile, byte i) : this(yapfile) {
         _encoding = i;
      }
      
      private YapWriter openTimeIO() {
         YapWriter yapwriter1 = _stream.getWriter(_stream.getTransaction(), _address, 8);
         yapwriter1.moveForward(4);
         return yapwriter1;
      }
      
      private void openTimeOverWritten() {
         if (lockFile()) {
            YapWriter yapwriter1 = openTimeIO();
            yapwriter1.read();
            if (YLong.readLong(yapwriter1) != _opentime) Db4o.throwRuntimeException(22);
            writeOpenTime();
         }
      }
      
      internal Transaction getTransactionToCommit() {
         return _transactionToCommit;
      }
      
      internal void go() {
         _stream.createStringIO(_encoding);
         if (lockFile()) {
            try {
               {
                  writeAccessTime();
               }
            }  catch (Exception exception) {
               {
               }
            }
            syncFiles();
            openTimeOverWritten();
            new Thread(this).start();
         }
      }
      
      private YapWriter headerLockIO() {
         YapWriter yapwriter1 = _stream.getWriter(_stream.getTransaction(), 0, 4);
         yapwriter1.moveForward(6);
         return yapwriter1;
      }
      
      private void headerLockOverwritten() {
         if (lockFile()) {
            YapWriter yapwriter1 = headerLockIO();
            yapwriter1.read();
            if (YInt.readInt(yapwriter1) != (int)_opentime) throw new DatabaseFileLockedException();
            writeHeaderLock();
         }
      }
      
      private bool lockFile() {
         return _stream.needsLockFileThread();
      }
      
      static internal long processID() {
         long l1 = j4o.lang.JavaSystem.currentTimeMillis();
         return l1;
      }
      
      internal bool read(YapWriter yapwriter) {
         _address = yapwriter.readInt();
         if (_address == 2) return true;
         read();
         return false;
      }
      
      internal void read() {
         writeOpenTime();
         YapWriter yapwriter1 = _stream.getWriter(_stream.getSystemTransaction(), _address, 37);
         try {
            {
               _stream.readBytes(yapwriter1._buffer, _address, 37);
            }
         }  catch (Exception exception) {
            {
            }
         }
         int i1 = yapwriter1.readInt();
         if (i1 > 37 || i1 < 21) Db4o.throwRuntimeException(17);
         long l1 = YLong.readLong(yapwriter1);
         long l_0_1 = YLong.readLong(yapwriter1);
         _encoding = yapwriter1.readByte();
         if (i1 > 21) {
            int i_1_1 = YInt.readInt(yapwriter1);
            int i_2_1 = YInt.readInt(yapwriter1);
            if (i_1_1 > 0 && i_1_1 == i_2_1) {
               _transactionToCommit = new Transaction(_stream, null);
               _transactionToCommit.setAddress(i_1_1);
            }
         }
         if (i1 > 29) _bootRecordID = YInt.readInt(yapwriter1);
         if (i1 > 33) YInt.readInt(yapwriter1);
         if (lockFile() && l_0_1 != 0L) {
            _stream.logMsg(28, null);
            long l_3_1 = 10000L;
            long l_4_1 = j4o.lang.JavaSystem.currentTimeMillis();
            while (j4o.lang.JavaSystem.currentTimeMillis() < l_4_1 + l_3_1) Cool.sleepIgnoringInterruption(l_3_1);
            yapwriter1 = _stream.getWriter(_stream.getSystemTransaction(), _address, 16);
            yapwriter1.moveForward(4);
            yapwriter1.read();
            long l_5_1 = YLong.readLong(yapwriter1);
            long l_6_1 = YLong.readLong(yapwriter1);
            if (l_6_1 > l_0_1) throw new DatabaseFileLockedException();
         }
         if (lockFile()) {
            Cool.sleepIgnoringInterruption(100L);
            syncFiles();
            openTimeOverWritten();
         }
         if (i1 < 37) write();
         go();
      }
      
      public void run() {
      }
      
      internal void syncFiles() {
         _stream.syncFiles();
      }
      
      internal void write() {
         headerLockOverwritten();
         _address = _stream.getSlot(37);
         YapWriter yapwriter1 = _stream.getWriter(_stream.i_trans, _address, 37);
         YInt.writeInt(37, yapwriter1);
         YLong.writeLong(_opentime, yapwriter1);
         YLong.writeLong(_opentime, yapwriter1);
         yapwriter1.append(_encoding);
         YInt.writeInt(0, yapwriter1);
         YInt.writeInt(0, yapwriter1);
         YInt.writeInt(_bootRecordID, yapwriter1);
         YInt.writeInt(0, yapwriter1);
         yapwriter1.write();
         writePointer();
      }
      
      internal bool writeAccessTime() {
         return _stream.writeAccessTime();
      }
      
      private void writeOpenTime() {
         if (lockFile()) {
            YapWriter yapwriter1 = openTimeIO();
            YLong.writeLong(_opentime, yapwriter1);
            yapwriter1.write();
         }
      }
      
      private void writeHeaderLock() {
         if (lockFile()) {
            YapWriter yapwriter1 = headerLockIO();
            YInt.writeInt((int)_opentime, yapwriter1);
            yapwriter1.write();
         }
      }
      
      private void writePointer() {
         headerLockOverwritten();
         YapWriter yapwriter1 = _stream.getWriter(_stream.i_trans, 0, 4);
         yapwriter1.moveForward(2);
         YInt.writeInt(_address, yapwriter1);
         yapwriter1.write();
         writeHeaderLock();
      }
   }
}