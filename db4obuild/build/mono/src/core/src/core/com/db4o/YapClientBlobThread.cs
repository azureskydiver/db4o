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

   internal class YapClientBlobThread : Thread {
      private YapClient stream;
      private Queue4 queue = new Queue4();
      private bool terminated = false;
      
      internal YapClientBlobThread(YapClient yapclient) : base() {
         stream = yapclient;
         this.setPriority(1);
      }
      
      internal void add(MsgBlob msgblob) {
         lock (queue) {
            queue.add(msgblob);
         }
      }
      
      internal bool isTerminated() {
         return terminated;
      }
      
      public override void run() {
         try {
            {
               YapSocket yapsocket1 = stream.createParalellSocket();
               Object obj1 = null;
               MsgBlob msgblob1;
               lock (queue) {
                  msgblob1 = (MsgBlob)queue.next();
               }
               while (msgblob1 != null) {
                  msgblob1.write(stream, yapsocket1);
                  msgblob1.processClient(yapsocket1);
                  lock (stream.blobLock) {
                     lock (queue) {
                        msgblob1 = (MsgBlob)queue.next();
                     }
                     if (msgblob1 == null) {
                        terminated = true;
                        Msg.CLOSE.write(stream, yapsocket1);
                        try {
                           {
                              yapsocket1.close();
                           }
                        }  catch (Exception exception) {
                           {
                           }
                        }
                     }
                  }
               }
            }
         }  catch (Exception exception) {
            {
               j4o.lang.JavaSystem.printStackTrace(exception);
            }
         }
      }
   }
}