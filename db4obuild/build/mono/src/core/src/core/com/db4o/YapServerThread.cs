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

   internal class YapServerThread : Thread {
      private String i_clientName;
      private bool i_loggedin;
      private long i_lastClientMessage;
      private YapFile i_mainStream;
      private Transaction i_mainTrans;
      private int i_pingAttempts = 0;
      private int i_nullMessages;
      private bool i_rollbackOnClose = true;
      private bool i_sendCloseMessage = true;
      private YapServer i_server;
      private YapSocket i_socket;
      private YapFile i_substituteStream;
      private Transaction i_substituteTrans;
      private Config4Impl i_config;
      internal int i_threadID;
      
      internal YapServerThread(YapServer yapserver, YapFile yapfile, YapSocket yapsocket, int i, bool xbool) : base() {
         i_loggedin = xbool;
         i_lastClientMessage = j4o.lang.JavaSystem.currentTimeMillis();
         i_server = yapserver;
         i_config = (Config4Impl)i_server.configure();
         i_mainStream = yapfile;
         i_threadID = i;
         this.setName("db4o message server " + i);
         i_mainTrans = new Transaction(yapfile, yapfile.getSystemTransaction());
         try {
            {
               i_socket = yapsocket;
               i_socket.setSoTimeout(((Config4Impl)yapserver.configure()).i_timeoutServerSocket);
            }
         }  catch (Exception exception) {
            {
               i_socket.close();
               throw exception;
            }
         }
      }
      
      public void close() {
         closeSubstituteStream();
         try {
            {
               if (i_sendCloseMessage) Msg.CLOSE.write(i_mainStream, i_socket);
            }
         }  catch (Exception exception) {
            {
            }
         }
         if (i_mainStream != null && i_mainTrans != null) i_mainTrans.close(i_rollbackOnClose);
         try {
            {
               i_socket.close();
            }
         }  catch (Exception exception) {
            {
            }
         }
         i_socket = null;
         try {
            {
               i_server.removeThread(this);
            }
         }  catch (Exception exception) {
            {
            }
         }
      }
      
      private void closeSubstituteStream() {
         if (i_substituteStream != null) {
            if (i_substituteTrans != null) {
               i_substituteTrans.close(i_rollbackOnClose);
               i_substituteTrans = null;
            }
            try {
               {
                  i_substituteStream.close();
               }
            }  catch (Exception exception) {
               {
               }
            }
            i_substituteStream = null;
         }
      }
      
      private YapFile getStream() {
         if (i_substituteStream != null) return i_substituteStream;
         return i_mainStream;
      }
      
      internal Transaction getTransaction() {
         if (i_substituteTrans != null) return i_substituteTrans;
         return i_mainTrans;
      }
      
      public override void run() {
         while (i_socket != null) {
            try {
               {
                  if (!messageProcessor()) break;
               }
            }  catch (Exception exception) {
               {
                  if (i_mainStream == null || i_mainStream.isClosed()) break;
               }
            }
            if (i_nullMessages > 20 || j4o.lang.JavaSystem.currentTimeMillis() - i_lastClientMessage > (long)i_config.i_timeoutPingClients) {
               if (i_pingAttempts > 5) {
                  getStream().logMsg(33, i_clientName);
                  break;
               }
               Msg.PING.write(getStream(), i_socket);
               i_pingAttempts++;
            }
         }
         close();
      }
      
      private bool messageProcessor() {
         Msg msg1 = Msg.readMessage(getTransaction(), i_socket);
         if (msg1 == null) {
            i_nullMessages++;
            return true;
         }
         i_lastClientMessage = j4o.lang.JavaSystem.currentTimeMillis();
         i_nullMessages = 0;
         i_pingAttempts = 0;
         if (!i_loggedin) {
            if (Msg.LOGIN.Equals(msg1)) {
               String xstring1 = ((MsgD)msg1).readString();
               String string_0_1 = ((MsgD)msg1).readString();
               User user1 = new User();
               user1.name = xstring1;
               i_mainStream.showInternalClasses(true);
               User user_1_1 = (User)i_mainStream.get(user1).next();
               i_mainStream.showInternalClasses(false);
               if (user_1_1 != null) {
                  if (user_1_1.password.Equals(string_0_1)) {
                     i_clientName = xstring1;
                     i_mainStream.logMsg(32, i_clientName);
                     Msg.OK.write(i_mainStream, i_socket);
                     i_loggedin = true;
                     this.setName("db4o server socket for client " + i_clientName);
                  } else {
                     Msg.FAILED.write(i_mainStream, i_socket);
                     return false;
                  }
               } else {
                  Msg.FAILED.write(i_mainStream, i_socket);
                  return false;
               }
            }
            return true;
         }
         if (msg1.processMessageAtServer(i_socket)) return true;
         if (Msg.CLOSE.Equals(msg1)) {
            Msg.CLOSE.write(getStream(), i_socket);
            getTransaction().commit();
            i_sendCloseMessage = false;
            getStream().logMsg(34, i_clientName);
            return false;
         }
         if (Msg.IDENTITY.Equals(msg1)) {
            respondInt((int)getStream().getID(getStream().i_bootRecord.i_db));
            return true;
         }
         if (Msg.CURRENT_VERSION.Equals(msg1)) {
            long l1 = getStream().i_bootRecord.i_versionGenerator;
            Msg.ID_LIST.getWriterForLong(getTransaction(), l1).write(getStream(), i_socket);
            return true;
         }
         if (Msg.RAISE_VERSION.Equals(msg1)) {
            long l1 = ((MsgD)msg1).readLong();
            YapFile yapfile1 = getStream();
            lock (yapfile1) {
               yapfile1.raiseVersion(l1);
            }
            return true;
         }
         if (Msg.GET_THREAD_ID.Equals(msg1)) {
            respondInt(i_threadID);
            return true;
         }
         if (Msg.SWITCH_TO_FILE.Equals(msg1)) {
            switchToFile(msg1);
            return true;
         }
         if (Msg.SWITCH_TO_MAIN_FILE.Equals(msg1)) {
            switchToMainFile();
            return true;
         }
         if (Msg.USE_TRANSACTION.Equals(msg1)) {
            useTransaction(msg1);
            return true;
         }
         return true;
      }
      
      private void switchToFile(Msg msg) {
         lock (i_mainStream.i_lock) {
            String xstring1 = ((MsgD)msg).readString();
            try {
               {
                  closeSubstituteStream();
                  i_substituteStream = (YapFile)Db4o.openFile(xstring1);
                  i_substituteTrans = new Transaction(i_substituteStream, i_substituteStream.getSystemTransaction());
                  i_substituteStream.i_config.i_messageRecipient = i_mainStream.i_config.i_messageRecipient;
                  Msg.OK.write(getStream(), i_socket);
               }
            }  catch (Exception exception) {
               {
                  closeSubstituteStream();
                  Msg.ERROR.write(getStream(), i_socket);
               }
            }
         }
      }
      
      private void switchToMainFile() {
         lock (i_mainStream.i_lock) {
            closeSubstituteStream();
            Msg.OK.write(getStream(), i_socket);
         }
      }
      
      private void useTransaction(Msg msg) {
         int i1 = ((MsgD)msg).readInt();
         YapServerThread yapserverthread_2_1 = i_server.findThread(i1);
         if (yapserverthread_2_1 != null) {
            Transaction transaction1 = yapserverthread_2_1.getTransaction();
            if (i_substituteTrans != null) i_substituteTrans = transaction1; else i_mainTrans = transaction1;
            i_rollbackOnClose = false;
         }
      }
      
      private void respondInt(int i) {
         Msg.ID_LIST.getWriterForInt(getTransaction(), i).write(getStream(), i_socket);
      }
   }
}