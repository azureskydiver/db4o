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

   abstract internal class YapMeta {
      
      internal YapMeta() : base() {
      }
      internal int i_id = 0;
      protected int i_state = 2;
      
      internal bool beginProcessing() {
         if (bitIsTrue(2)) return false;
         bitTrue(2);
         return true;
      }
      
      internal void bitFalse(int i) {
         i_state &= 1 << i ^ -1;
      }
      
      internal bool bitIsFalse(int i) {
         return (i_state | 1 << i) != i_state;
      }
      
      internal bool bitIsTrue(int i) {
         return (i_state | 1 << i) == i_state;
      }
      
      internal void bitTrue(int i) {
         i_state |= 1 << i;
      }
      
      internal virtual void cacheDirty(Collection4 collection4) {
         if (!bitIsTrue(3)) {
            bitTrue(3);
            collection4.add(this);
         }
      }
      
      internal void endProcessing() {
         bitFalse(2);
      }
      
      public virtual int getID() {
         return i_id;
      }
      
      abstract internal byte getIdentifier();
      
      public bool isActive() {
         return bitIsTrue(1);
      }
      
      public virtual bool isDirty() {
         return bitIsTrue(1) && !bitIsTrue(0);
      }
      
      public int linkLength() {
         return 4;
      }
      
      internal void notCachedDirty() {
         bitFalse(3);
      }
      
      abstract internal int ownLength();
      
      internal virtual void read(Transaction transaction) {
         try {
            {
               if (beginProcessing()) {
                  YapReader yapreader1 = transaction.i_stream.readReaderByID(transaction, getID());
                  if (yapreader1 != null) {
                     readThis(transaction, yapreader1);
                     setStateOnRead(yapreader1);
                  }
                  endProcessing();
               }
            }
         }  catch (LongJumpOutException longjumpoutexception) {
            {
               throw longjumpoutexception;
            }
         } catch (Exception throwable) {
            {
            }
         }
      }
      
      abstract internal void readThis(Transaction transaction, YapReader yapreader);
      
      internal virtual void setID(YapStream yapstream, int i) {
         i_id = i;
      }
      
      internal void setStateClean() {
         bitTrue(1);
         bitTrue(0);
      }
      
      internal void setStateDeactivated() {
         bitFalse(1);
      }
      
      internal void setStateDirty() {
         bitTrue(1);
         bitFalse(0);
      }
      
      internal void setStateOnRead(YapReader yapreader) {
         if (bitIsTrue(3)) setStateDirty(); else setStateClean();
      }
      
      internal YapWriter write(YapStream yapstream, Transaction transaction) {
         if (writeObjectBegin()) {
            YapWriter yapwriter1 = getID() == 0 ? yapstream.newObject(transaction, this) : yapstream.updateObject(transaction, this);
            writeThis(yapwriter1);
            ((YapFile)yapstream).writeObject(this, yapwriter1);
            if (isActive()) setStateClean();
            endProcessing();
            return yapwriter1;
         }
         return null;
      }
      
      internal virtual bool writeObjectBegin() {
         if (isDirty()) return beginProcessing();
         return false;
      }
      
      internal virtual void writeOwnID(YapWriter yapwriter) {
         write(yapwriter.getStream(), yapwriter.getTransaction());
         yapwriter.writeInt(getID());
      }
      
      abstract internal void writeThis(YapWriter yapwriter);
      
      static internal void writeIDOf(YapMeta yapmeta, YapWriter yapwriter) {
         if (yapmeta != null) yapmeta.writeOwnID(yapwriter); else yapwriter.writeInt(0);
      }
   }
}