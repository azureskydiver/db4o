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

   internal class Msg : Cloneable {
      static internal int idGenererator = 1;
      internal int i_msgID;
      internal String i_name;
      internal Transaction i_trans;
      private static Msg[] i_messages = new Msg[60];
      public static MsgD CLASS_NAME_FOR_ID = new MClassNameForID();
      public static Msg CLOSE = new Msg("CLOSE");
      public static Msg COMMIT = new MCommit();
      public static MsgD CREATE_CLASS = new MCreateClass();
      public static Msg CURRENT_VERSION = new Msg("VERSION");
      public static MsgD DELETE = new MDelete();
      public static Msg ERROR = new Msg("ERROR");
      public static Msg FAILED = new Msg("FAILED");
      public static Msg GET_ALL = new MGetAll();
      public static MsgD GET_CLASSES = new MGetClasses();
      public static MsgD GET_INTERNAL_IDS = new MGetInternalIDs();
      public static Msg GET_THREAD_ID = new Msg("GET_THREAD_ID");
      public static MsgD ID_LIST = new MsgD("ID_LIST");
      public static Msg IDENTITY = new Msg("IDENTITY");
      public static MsgD LENGTH = new MsgD("LENGTH");
      public static MsgD LOGIN = new MsgD("LOGIN");
      public static Msg NULL = new Msg("NULL");
      public static MsgD OBJECT_BY_UUID = new MObjectByUuid();
      public static MsgObject OBJECT_TO_CLIENT = new MsgObject();
      public static Msg OK = new Msg("OK");
      public static Msg PING = new Msg("PING");
      public static Msg PREFETCH_IDS = new MPrefetchIDs();
      public static MsgObject QUERY_EXECUTE = new MQueryExecute();
      public static MsgD RAISE_VERSION = new MsgD("RAISE_VERSION");
      public static MsgBlob READ_BLOB = new MReadBlob();
      public static MsgD READ_BYTES = new MReadBytes();
      public static MsgD READ_MULTIPLE_OBJECTS = new MReadMultipleObjects();
      public static MsgD READ_OBJECT = new MReadObject();
      public static MsgD RELEASE_SEMAPHORE = new MReleaseSemaphore();
      public static Msg ROLLBACK = new MRollback();
      public static MsgD SET_SEMAPHORE = new MSetSemaphore();
      public static Msg SUCCESS = new Msg("SUCCESS");
      public static MsgD SWITCH_TO_FILE = new MsgD("SWITCH_F");
      public static Msg SWITCH_TO_MAIN_FILE = new Msg("SWITCH_M");
      public static Msg TA_BEGIN_END_SET = new MTaBeginEndSet();
      public static MsgD TA_DELETE = new MTaDelete();
      public static MsgD TA_DONT_DELETE = new MTaDontDelete();
      public static MsgD TA_IS_DELETED = new MTaIsDeleted();
      public static MsgD USER_MESSAGE = new MUserMessage();
      public static MsgD USE_TRANSACTION = new MUseTransaction();
      public static MsgBlob WRITE_BLOB = new MWriteBlob();
      public static MWriteNew WRITE_NEW = new MWriteNew();
      public static MsgObject WRITE_UPDATE = new MWriteUpdate();
      public static MsgD WRITE_UPDATE_DELETE_MEMBERS = new MWriteUpdateDeleteMembers();
      
      internal Msg() : base() {
         i_msgID = idGenererator++;
         i_messages[i_msgID] = this;
      }
      
      internal Msg(String xstring) : this() {
         i_name = xstring;
      }
      
      internal Msg clone(Transaction transaction) {
         try {
            {
               Msg msg_0_1 = (Msg)j4o.lang.JavaSystem.clone(this);
               msg_0_1.i_trans = transaction;
               return msg_0_1;
            }
         }  catch (CloneNotSupportedException clonenotsupportedexception) {
            {
               return null;
            }
         }
      }
      
      public override bool Equals(Object obj) {
         if (this == obj) return true;
         if (obj == null || j4o.lang.Class.getClassForObject(obj) != j4o.lang.Class.getClassForObject(this)) return false;
         return i_msgID == ((Msg)obj).i_msgID;
      }
      
      internal virtual void fakePayLoad(Transaction transaction) {
         i_trans = transaction;
      }
      
      internal virtual YapWriter getByteLoad() {
         return null;
      }
      
      internal String getName() {
         if (i_name == null) return j4o.lang.Class.getClassForObject(this).getName();
         return i_name;
      }
      
      internal Transaction getTransaction() {
         return i_trans;
      }
      
      internal YapStream getStream() {
         return getTransaction().i_stream;
      }
      
      internal virtual bool processMessageAtServer(YapSocket yapsocket) {
         return false;
      }
      
      static internal Msg readMessage(Transaction transaction, YapSocket yapsocket) {
         YapWriter yapwriter1 = new YapWriter(transaction, 9);
         try {
            {
               yapwriter1.read(yapsocket);
               Msg msg1 = i_messages[yapwriter1.readInt()].readPayLoad(transaction, yapsocket, yapwriter1);
               return msg1;
            }
         }  catch (Exception exception) {
            {
               return null;
            }
         }
      }
      
      internal virtual Msg readPayLoad(Transaction transaction, YapSocket yapsocket, YapWriter yapwriter) {
         if (yapwriter.readByte() == 115 && transaction.i_parentTransaction != null) transaction = transaction.i_parentTransaction;
         return clone(transaction);
      }
      
      internal void setTransaction(Transaction transaction) {
         i_trans = transaction;
      }
      
      public override String ToString() {
         return getName();
      }
      
      internal void write(YapStream yapstream, YapSocket yapsocket) {
         lock (yapsocket) {
            try {
               {
                  yapsocket.write(getPayLoad()._buffer);
                  yapsocket.flush();
               }
            }  catch (IOException ioexception) {
               {
               }
            }
         }
      }
      
      internal virtual YapWriter getPayLoad() {
         YapWriter yapwriter1 = new YapWriter(getTransaction(), 9);
         yapwriter1.writeInt(i_msgID);
         return yapwriter1;
      }
      
      internal void writeQueryResult(Transaction transaction, QResult qresult, YapSocket yapsocket) {
         int i1 = qresult.size();
         MsgD msgd1 = ID_LIST.getWriterForLength(transaction, 4 * (i1 + 1));
         YapWriter yapwriter1 = msgd1.getPayLoad();
         yapwriter1.writeQueryResult(qresult);
         msgd1.write(transaction.i_stream, yapsocket);
      }
   }
}