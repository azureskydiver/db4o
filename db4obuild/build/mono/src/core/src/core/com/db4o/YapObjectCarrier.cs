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
using com.db4o.ext;
namespace com.db4o {

   internal class YapObjectCarrier : YapMemoryFile {
      
      internal YapObjectCarrier(YapStream yapstream, MemoryFile memoryfile) : base(yapstream, memoryfile) {
      }
      
      internal override void initialize0b() {
      }
      
      internal override void initialize1() {
         i_handlers = i_parent.i_handlers;
         i_classCollection = i_parent.i_classCollection;
         i_config = i_parent.i_config;
         i_stringIo = i_parent.i_stringIo;
         i_references = new YapReferences(this);
         this.initialize2();
      }
      
      internal override void initialize2b() {
      }
      
      internal override void initializeEssentialClasses() {
      }
      
      internal override void initialize4NObjectCarrier() {
      }
      
      internal override void initNewClassCollection() {
      }
      
      internal override bool canUpdate() {
         return false;
      }
      
      internal override void configureNewFile() {
         i_writeAt = 18;
      }
      
      public override bool close() {
         lock (i_lock) {
            bool xbool1 = this.close1();
            if (xbool1) i_config = null;
            return xbool1;
         }
      }
      
      internal override void createTransaction() {
         i_trans = new TransactionObjectCarrier(this, null);
         i_systemTrans = i_trans;
      }
      
      internal override long currentVersion() {
         return 0L;
      }
      
      public override bool dispatchsEvents() {
         return false;
      }
      
      protected void finalize() {
      }
      
      internal override void free(int i, int i_0_) {
      }
      
      internal override int getSlot(int i) {
         int i_1_1 = i_writeAt;
         i_writeAt += i;
         return i_1_1;
      }
      
      public override Db4oDatabase identity() {
         return i_parent.identity();
      }
      
      internal override bool maintainsIndices() {
         return false;
      }
      
      internal override void message(String xstring) {
      }
      
      internal override bool needsLockFileThread() {
         return false;
      }
      
      internal override void raiseVersion(long l) {
      }
      
      internal override void readThis() {
      }
      
      internal override bool stateMessages() {
         return false;
      }
      
      internal override void write(bool xbool) {
         this.checkNeededUpdates();
         this.writeDirty();
         this.getTransaction().commit();
      }
      
      internal override void writeHeader(bool xbool) {
      }
      
      internal override void writeBootRecord() {
      }
   }
}