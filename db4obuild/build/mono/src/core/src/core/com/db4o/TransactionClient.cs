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

   internal class TransactionClient : Transaction {
      private YapClient i_client;
      private Tree i_yapObjectsToGc;
      
      internal TransactionClient(YapClient yapclient, Transaction transaction) : base((YapStream)yapclient, transaction) {
         i_client = yapclient;
      }
      
      internal override void beginEndSet() {
         if (i_delete != null) i_delete.traverse(new TransactionClient__1(this));
         i_delete = null;
         i_writtenUpdateDeletedMembers = null;
         i_client.writeMsg(Msg.TA_BEGIN_END_SET);
      }
      
      internal override void commit() {
         this.commitTransactionListeners();
         if (i_yapObjectsToGc != null) i_yapObjectsToGc.traverse(new TransactionClient__2(this));
         i_yapObjectsToGc = null;
         i_client.writeMsg(Msg.COMMIT);
      }
      
      internal override void delete(YapObject yapobject, Object obj, int i, bool xbool) {
         base.delete(yapobject, obj, i, false);
         i_client.writeMsg(Msg.TA_DELETE.getWriterFor2Ints(this, yapobject.getID(), i));
      }
      
      internal override void dontDelete(int i, bool xbool) {
         base.dontDelete(i, false);
         i_client.writeMsg(Msg.TA_DONT_DELETE.getWriterForInt(this, i));
      }
      
      internal override bool isDeleted(int i) {
         i_client.writeMsg(Msg.TA_IS_DELETED.getWriterForInt(this, i));
         int i_0_1 = i_client.expectedByteResponse(Msg.TA_IS_DELETED).readInt();
         return i_0_1 == 1;
      }
      
      internal override Object[] objectAndYapObjectBySignature(long l, byte[] xis) {
         int i1 = 12 + xis.Length;
         MsgD msgd1 = Msg.OBJECT_BY_UUID.getWriterForLength(this, i1);
         msgd1.writeLong(l);
         msgd1.writeBytes(xis);
         i_client.writeMsg(msgd1);
         msgd1 = (MsgD)i_client.expectedResponse(Msg.OBJECT_BY_UUID);
         int i_1_1 = msgd1.readInt();
         if (i_1_1 > 0) return i_stream.getObjectAndYapObjectByID(this, i_1_1);
         return new Object[2];
      }
      
      public override void rollback() {
         i_yapObjectsToGc = null;
         this.rollBackTransactionListeners();
      }
      
      internal override void writeUpdateDeleteMembers(int i, YapClass yapclass, int i_2_, int i_3_) {
         i_client.writeMsg(Msg.WRITE_UPDATE_DELETE_MEMBERS.getWriterFor4Ints(this, i, yapclass.getID(), i_2_, i_3_));
      }
      
      static internal Tree access__002(TransactionClient transactionclient, Tree tree) {
         return transactionclient.i_yapObjectsToGc = tree;
      }
      
      static internal Tree access__000(TransactionClient transactionclient) {
         return transactionclient.i_yapObjectsToGc;
      }
   }
}