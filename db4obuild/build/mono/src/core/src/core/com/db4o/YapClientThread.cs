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

   internal class YapClientThread : Thread {
      private Thread streamThread;
      private YapClient i_stream;
      private YapSocket i_socket;
      internal Queue4 messageQueue;
      internal Lock4 messageQueueLock;
      
      internal YapClientThread(YapClient yapclient, YapSocket yapsocket, Queue4 queue4, Lock4 lock4) : base() {
         lock (this) {
            i_stream = yapclient;
            messageQueue = queue4;
            i_socket = yapsocket;
            streamThread = Thread.currentThread();
            messageQueueLock = lock4;
         }
      }
      
      internal bool isClosed() {
         return i_socket == null;
      }
      
      internal void close() {
         i_stream = null;
         i_socket = null;
      }
      
      public override void run() {
         while (i_socket != null) {
            do {
               try {
                  {
                     Msg msg1 = Msg.readMessage(i_stream.getTransaction(), i_socket);
                     if (Msg.PING.Equals(msg1)) i_stream.writeMsg(Msg.OK); else {
                        if (Msg.CLOSE.Equals(msg1)) {
                           i_stream.logMsg(35, i_stream.ToString());
                           close();
                           lock (i_stream) {
                              j4o.lang.JavaSystem.notify(i_stream);
                              break;
                           }
                        }
                        if (msg1 != null) messageQueueLock.run(new YapClientThread__1(this, msg1));
                     }
                  }
               }  catch (Exception exception) {
                  {
                     break;
                  }
               }
            }             while (false);
         }
      }
   }
}